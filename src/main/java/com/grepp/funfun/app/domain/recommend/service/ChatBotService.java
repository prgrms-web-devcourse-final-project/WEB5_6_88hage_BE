package com.grepp.funfun.app.domain.recommend.service;

import com.grepp.funfun.app.domain.content.document.ContentEmbedding;
import com.grepp.funfun.app.domain.content.dto.ContentWithReasonDTO;
import com.grepp.funfun.app.domain.content.service.ContentService;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import com.grepp.funfun.app.domain.group.dto.GroupWithReasonDTO;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendContentDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendGroupDTO;
import com.grepp.funfun.app.domain.recommend.dto.StartAndDuringDTO;
import com.grepp.funfun.app.domain.recommend.dto.payload.ChatBotMessage;
import com.grepp.funfun.app.domain.recommend.dto.payload.ChatBotRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendContentResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendGroupResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendTwoListResponse;
import com.grepp.funfun.app.domain.recommend.entity.ChatBot;
import com.grepp.funfun.app.domain.recommend.repository.ChatBotRepository;
import com.grepp.funfun.app.domain.recommend.repository.ContentEmbeddingRepository;
import com.grepp.funfun.app.domain.recommend.repository.GroupEmbeddingRepository;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import dev.langchain4j.data.embedding.Embedding;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final EmbeddingModel embeddingModel;
    private final ChatBotRepository chatBotRepository;
    private final GroupEmbeddingRepository groupEmbeddingRepository;
    private final ContentEmbeddingRepository contentEmbeddingRepository;

    private final UserService userService;
    private final AiRequestQueue aiRequestQueue;
    private final DynamicService dynamicService;
    private final GroupService groupService;
    private final ContentService contentService;
    private final ChatBotAiService chatBotAiService;

    public static String getDate(LocalDateTime startTime, LocalDateTime endTime) {

        String[] dayOfWeek = {"", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};

        int year = startTime.getYear();
        int month = startTime.getMonthValue();
        int day = startTime.getDayOfMonth();
        int hour = startTime.getHour();
        int minute = startTime.getMinute();
        int dayOfWeekValue = startTime.getDayOfWeek()
                                      .getValue(); // 월요일=1, 화요일=2, ..., 일요일=7

        int year2 = endTime.getYear();
        int month2 = endTime.getMonthValue();
        int day2 = endTime.getDayOfMonth();
        int hour2 = endTime.getHour();
        int minute2 = endTime.getMinute();
        int dayOfWeekValue2 = endTime.getDayOfWeek()
                                     .getValue();

        String dayName = dayOfWeek[dayOfWeekValue];
        String dayName2 = dayOfWeek[dayOfWeekValue2];

        log.info("시작:, {} {} 끝: {} {}", dayOfWeekValue, dayName, dayOfWeekValue2, dayName2);

        // 문자열 조합
        String start = String.format("나는 %d-%02d-%02d %s %d시 %02d분 부터 ",
                                     year, month, day, dayName, hour, minute);
        String end = String.format("%d-%02d-%02d %s %d시 %02d분 까지 여기시간이야 ",
                                   year2, month2, day2, dayName2, hour2, minute2);
        return start + end;
    }

    public Optional<ChatBot> findChatBotSummary(String email) {
        Optional<ChatBot> chatBot = chatBotRepository.findByEmailAndActivatedIsTrue(email);
        return chatBot;
    }

    @Transactional
    public void updateSummary(Long id, String summary) {
        chatBotRepository.updateSummary(id, summary);
    }

    @Transactional
    public void registSummary(ChatBot chatBot) {
        chatBotRepository.save(chatBot);
    }

    public StartAndDuringDTO getTime(LocalDateTime startTime, LocalDateTime endTime) {

        LocalDateTime userStart = startTime.plusHours(1);
        LocalDateTime userEnd = endTime.minusHours(1);
        long userAvailableHours = Duration.between(userStart, endTime).toHours();

        log.info("사용자 이동시간 고려: {} ~ {}, 소요시간: {}", userStart, userEnd, userAvailableHours );

        Long startDate = userStart
                                 .toInstant(ZoneOffset.UTC)
                                 .toEpochMilli();
        Long endDate = userEnd
                               .toInstant(ZoneOffset.UTC)
                               .toEpochMilli();


        return new StartAndDuringDTO(startDate, endDate);

    }
    @Transactional(readOnly = true)
    public EmbeddingStoreContentRetriever getContentDocument(Long startTime, Long endTime){
        List<ContentEmbedding> contentEmbeddings = contentEmbeddingRepository.findByStartTimeEpochGreaterThanEqualAndEndTimeEpochLessThanEqual(startTime, endTime);

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        for (ContentEmbedding content : contentEmbeddings) {
            log.info("필터링된 id: {}", content.getId());
            TextSegment segment = TextSegment.from(content.getText());
            Embedding embedding = new Embedding(content.getEmbedding());
            embeddingStore.add(embedding, segment);
        }

        return EmbeddingStoreContentRetriever.builder()
                                             .embeddingStore(embeddingStore)
                                             .embeddingModel(embeddingModel) // 반드시 기존에 주입된 모델과 동일해야 함
                                             .maxResults(10)
                                             .minScore(0.65)
                                             .build();
    }


    @Transactional(readOnly = true)
    public EmbeddingStoreContentRetriever getGroupDocument(Long startTime, Long endTime){
        List<GroupEmbedding> groupEmbeddings = groupEmbeddingRepository.findByStartTimeEpochGreaterThanEqualAndEndTimeEpochLessThanEqual(startTime, endTime);

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        for (GroupEmbedding group : groupEmbeddings) {
            log.info("필터링된 id: {}", group.getId());
            TextSegment segment = TextSegment.from(group.getText());
            Embedding embedding = new Embedding(group.getEmbedding());
            embeddingStore.add(embedding, segment);
        }

        return EmbeddingStoreContentRetriever.builder()
                                             .embeddingStore(embeddingStore)
                                             .embeddingModel(embeddingModel) // 반드시 기존에 주입된 모델과 동일해야 함
                                             .maxResults(10)
                                             .minScore(0.65)
                                             .build();
    }

    public EmbeddingStoreContentRetriever getContentPlaceDocument(String address) {
        List<ContentEmbedding> contentEmbeddings = contentEmbeddingRepository.findByGunameContainingAndEventType(address, "PLACE");

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        for (ContentEmbedding content : contentEmbeddings) {
            log.info("필터링된 id: {}, 구: {}, 유형: {}", content.getId(), content.getGuname(), content.getEventType());
            TextSegment segment = TextSegment.from(content.getText());
            Embedding embedding = new Embedding(content.getEmbedding());
            embeddingStore.add(embedding, segment);
        }

        return EmbeddingStoreContentRetriever.builder()
                                             .embeddingStore(embeddingStore)
                                             .embeddingModel(embeddingModel) // 반드시 기존에 주입된 모델과 동일해야 함
                                             .maxResults(10)
                                             .minScore(0.6)
                                             .build();
    }

    public List<ChatBotDTO> findAll() {
        final List<ChatBot> chatBots = chatBotRepository.findAll(Sort.by("id"));
        return chatBots.stream()
                       .map(chatBot -> mapToDTO(chatBot, new ChatBotDTO()))
                       .toList();
    }

    public ChatBotDTO get(final Long id) {
        return chatBotRepository.findById(id)
                                .map(chatBot -> mapToDTO(chatBot, new ChatBotDTO()))
                                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ChatBotDTO chatBotDTO) {
        final ChatBot chatBot = new ChatBot();
        mapToEntity(chatBotDTO, chatBot);
        return chatBotRepository.save(chatBot)
                                .getId();
    }

    public void update(final Long id, final ChatBotDTO chatBotDTO) {
        final ChatBot chatBot = chatBotRepository.findById(id)
                                                 .orElseThrow(() -> new CommonException(
                                                     ResponseCode.NOT_FOUND));
        mapToEntity(chatBotDTO, chatBot);
        chatBotRepository.save(chatBot);
    }

    public void delete(final Long id) {
        chatBotRepository.deleteById(id);
    }

    private ChatBotDTO mapToDTO(final ChatBot chatBot, final ChatBotDTO chatBotDTO) {
        chatBotDTO.setId(chatBot.getId());
        chatBotDTO.setEmail(chatBot.getEmail());
        chatBotDTO.setGroupSummary(chatBot.getGroupSummary());
        chatBotDTO.setContentSummary(chatBot.getContentSummary());
        chatBotDTO.setType(chatBot.getType());
        return chatBotDTO;
    }

    private ChatBot mapToEntity(final ChatBotDTO chatBotDTO, final ChatBot chatBot) {
        chatBot.builder()
               .email(chatBotDTO.getEmail())
               .contentSummary(chatBotDTO.getContentSummary())
               .groupSummary(chatBotDTO.getGroupSummary())
               .type(chatBotDTO.getType())
               .build();
        return chatBot;
    }


    public String getPrompt(String summary, String agePrompt, String address) {
        String prompt = agePrompt + "나는 지금 " + address + "에 있어 ";
        if (summary != null) {
            prompt += summary;
            prompt += " 이건 나의 취향을 분석한 후 요약한 내용인데 이걸 고려해서 ";
        }
        prompt += " 장소에 맞게 실제로 실행할 수 있는 활동들을 추천해줘";
        
        return prompt;
    }

    public String recommendCategoryPrompt(String prompt, String eventType) {

        String categoryPrompt = prompt;
        categoryPrompt += "\n\n 대화 내용을 분석해서 3줄이내로 요약한 후 결과적으로";

        if(eventType.equals("CONTENT")) {
            for (ContentClassification classification : ContentClassification.values()) {
                categoryPrompt += classification.getKoreanName();
                categoryPrompt += ", ";
            }

            String newString = categoryPrompt.substring(0, categoryPrompt.length() - 2);
            newString += " 이중에서 나에게 어울릴만한 활동들은 뭐가 있을지 한 줄로 작성해줘.";

            return newString;
        } else {
            for (GroupClassification classification : GroupClassification.values()) {
                categoryPrompt += classification.getKoreanName();
                categoryPrompt += ", ";
            }

            String newString = categoryPrompt.substring(0, categoryPrompt.length() - 2);
            newString += " 이중에서 나에게 어울릴만한 활동들은 뭐가 있을지 한 줄로 작성해줘.";

            return newString;
        }
    }

    public CompletableFuture<RecommendGroupResponse> quickRecommendGroup(RecommendRequest request, Authentication authentication) {
        String email = authentication.getName();
        String preference = userService.getUserPreferenceDescription(email, "GROUP");

        String prompt = "나는 " + request.getAddress() + "에 있어 " + preference
            + " 내가 선호하는 활동을 고려해서 장소에 맞게 활동들을 추천해줘";
        log.info("🟢 프롬프트 생성: {}", prompt);

        StartAndDuringDTO userTime = getTime(request.getStartTime(), request.getEndTime());

        CompletableFuture<RecommendGroupDTO> groupFuture = new CompletableFuture<>();

        EmbeddingStoreContentRetriever filteredRetriever = getGroupDocument(userTime.startDate(), userTime.endDate());

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(
                () -> dynamicService.quickRecommendGroup(prompt, userTime.startDate(), userTime.endDate(), filteredRetriever),
                groupFuture
            )
        );

        return groupFuture.thenApply(groups -> {
            //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //log.info("🔴 CompletableFuture 실행 시 인증 정보: {}", auth);

            if (groups == null || groups.group() == null) {
                return RecommendGroupResponse.builder().groups(List.of()).build();
            }

            List<RecommendDTO> recommendList = groups.group();
            Map<Long, String> reasonMap = recommendList.stream()
                                                       .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));

            List<Long> recommendIds = recommendList.stream()
                                                   .map(RecommendDTO::id)
                                                   .filter(Objects::nonNull)
                                                   .toList();

            log.info("모임 추천 결과 ==================");
            for (RecommendDTO dto : recommendList) {
                log.info("id: {}", dto.id());
                log.info("추천이유 : {}", dto.reason());
            }

            List<GroupWithReasonDTO> recommendGroups = groupService.findByIds(recommendIds);
            recommendGroups.forEach(content ->
                                        content.setReason(reasonMap.get(content.getId()))
            );

            return RecommendGroupResponse.builder()
                                         .groups(recommendGroups)
                                         .build();
        });
    }

    public CompletableFuture<RecommendTwoListResponse> quickRecommendContent(RecommendRequest request, Authentication authentication) {
        String email = authentication.getName();
        String preference = userService.getUserPreferenceDescription(email, "CONTENT");
        String agePrompt = userService.getUserAgePromptByEmail(email);
        String userAddress = request.getAddress() != null ? request.getAddress() : userService.getUserAddress(email);

        StartAndDuringDTO userTime = getTime(request.getStartTime(), request.getEndTime());

        String promptForEvent = agePrompt + preference + "나는 지금 " + userAddress + "에 있어"
            + " 내가 선호하는 활동을 고려해서 장소에 맞게 활동들을 추천해줘";

        String promptForPlace = userAddress + " 근처에서 할만한 활동을 추천해줘";

        log.info("행사 프롬프트: {}", promptForEvent);

        CompletableFuture<RecommendContentDTO> contentFuture = new CompletableFuture<>();
        CompletableFuture<RecommendContentDTO> placeFuture = new CompletableFuture<>();

        EmbeddingStoreContentRetriever filteredRetriever = getContentDocument(userTime.startDate(), userTime.endDate());
        EmbeddingStoreContentRetriever filteredRetriever2 = getContentPlaceDocument(extractSecondToken(userAddress));
        // 비동기 요청 등록
        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(
                () -> dynamicService.quickRecommendEvent(promptForEvent, userTime.startDate(), userTime.endDate(), filteredRetriever),
                contentFuture
            )
        );

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(
                () -> dynamicService.quickRecommendPlace(promptForPlace, userAddress, filteredRetriever2),
                placeFuture
            )
        );

        // 행사 추천 결과 처리
        CompletableFuture<List<ContentWithReasonDTO>> contentListFuture = contentFuture.thenApply(contents -> {
            if (contents == null || contents.event() == null) return List.of();

            Map<Long, String> reasonMap = contents.event().stream()
                                                  .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));

            List<Long> ids = contents.event().stream()
                                     .map(RecommendDTO::id)
                                     .filter(Objects::nonNull)
                                     .toList();

            log.info("컨텐츠 추천 결과 ==================");
            contents.event().forEach(dto ->
                                         log.info("id: {}, 추천이유 : {}", dto.id(), dto.reason()));

            List<ContentWithReasonDTO> results = contentService.findByIds(ids);
            results.forEach(content -> content.setReason(reasonMap.get(content.getId())));
            return results;
        });

        // 장소 추천 결과 처리
        CompletableFuture<List<ContentWithReasonDTO>> placeListFuture = placeFuture.thenApply(place -> {
            if (place == null || place.event() == null) return List.of();

            Map<Long, String> reasonMap = place.event().stream()
                                               .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));

            List<Long> ids = place.event().stream()
                                  .map(RecommendDTO::id)
                                  .filter(Objects::nonNull)
                                  .toList();

            log.info("장소 추천 결과 ==================");
            place.event().forEach(dto ->
                                      log.info("id: {}, 추천이유 : {}", dto.id(), dto.reason()));

            List<ContentWithReasonDTO> results = contentService.findByIds(ids);
            results.forEach(content -> content.setReason(reasonMap.get(content.getId())));
            return results;
        });

        return contentListFuture.thenCombine(placeListFuture, (contents, places) ->
            RecommendTwoListResponse.builder()
                                    .events(contents)
                                    .places(places)
                                    .build()
        );
    }

    public static String extractSecondToken(String address) {
        if (address == null || address.trim()
                                      .isEmpty()) {
            return null;
        }
        String[] tokens = address.split(" ");

        // 토큰이 2개 이상일 경우에만 두 번째 토큰(인덱스 1)이 존재
        if (tokens.length >= 2) {
            return tokens[1];
        } else {
            return null; // 두 번째 토큰이 없는 경우
        }
    }

    public CompletableFuture<RecommendGroupResponse> chatBotRecommendGroup(RecommendRequest request, Authentication authentication) {
        String email = authentication.getName();
        Optional<ChatBot> chatBot = findChatBotSummary(email);
        String summary = null;
        if (chatBot.isPresent()) {
            summary = chatBot.get()
                             .getGroupSummary();
        }

        String agePrompt = userService.getUserAgePromptByEmail(email);
        String prompt = getPrompt(summary, agePrompt, request.getAddress());

        log.info("사용자 프롬프트: {}", prompt);

        StartAndDuringDTO userTime = getTime(request.getStartTime(), request.getEndTime());

        CompletableFuture<RecommendGroupDTO> groupFuture = new CompletableFuture<>();

        EmbeddingStoreContentRetriever filteredRetriever = getGroupDocument(userTime.startDate(), userTime.endDate());

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(
                () -> dynamicService.chatBotRecommendGroup(prompt, userTime.startDate(), userTime.endDate(), filteredRetriever),
                groupFuture
            )
        );

        return groupFuture.thenApply(groups -> {
            if (groups == null || groups.group() == null) {
                return RecommendGroupResponse.builder().groups(List.of()).build();
            }

            List<RecommendDTO> recommendList = groups.group();
            Map<Long, String> reasonMap = recommendList.stream()
                                                       .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));

            List<Long> recommendIds = recommendList.stream()
                                                   .map(RecommendDTO::id)
                                                   .filter(Objects::nonNull)
                                                   .toList();

            log.info("모임 추천 결과 ==================");
            recommendList.forEach(dto -> log.info("id: {}, 추천이유 : {}", dto.id(), dto.reason()));

            List<GroupWithReasonDTO> recommendGroups = groupService.findByIds(recommendIds);
            recommendGroups.forEach(content -> content.setReason(reasonMap.get(content.getId())));

            return RecommendGroupResponse.builder()
                                         .groups(recommendGroups)
                                         .build();
        });
    }

    public CompletableFuture<ApiResponse<String>> chatBotCloseAndSummary(ChatBotRequest request, Authentication authentication) {
        String email = authentication.getName();
        String conversation = buildChatBotPrompt(request.getChatBotHistory(), null);
        String prompt = recommendCategoryPrompt(conversation, request.getEventType());
        log.info("최종 대화내역 + 카테고리: {}", prompt);

        Optional<ChatBot> chatBotOpt = findChatBotSummary(email);
        CompletableFuture<String> future = new CompletableFuture<>();

        aiRequestQueue.addRequest(new AiRequestQueue.AiRequestTask<>(() -> {
            String summary = chatBotAiService.summary(prompt);
            log.info("취향 요약: {}", summary);

            if (chatBotOpt.isPresent()) {
                updateSummary(chatBotOpt.get().getId(), summary);
            } else {
                ChatBot.ChatBotBuilder builder = ChatBot.builder().email(email);
                if ("CONTENT".equalsIgnoreCase(request.getEventType())) {
                    builder.contentSummary(summary);
                } else if ("GROUP".equalsIgnoreCase(request.getEventType())) {
                    builder.groupSummary(summary);
                }
                registSummary(builder.build());
            }

            return summary;
        }, future));

        return future.thenApply(ApiResponse::success);
    }

    private String buildChatBotPrompt(List<ChatBotMessage> chatBotHistory, String userMessage) {
        StringBuilder prompt = appendChatBotHistory(chatBotHistory, userMessage);
        return prompt.toString();
    }

    private StringBuilder appendChatBotHistory(List<ChatBotMessage> chatBotHistory,
        String userMessage) {
        StringBuilder prompt = new StringBuilder("--대화 내용--");
        if (chatBotHistory != null && !chatBotHistory.isEmpty()) {
            for (ChatBotMessage msg : chatBotHistory) {
                prompt.append("\n사용자: ")
                      .append(msg.getUser());
                prompt.append("\n당신: ")
                      .append(msg.getAi());
            }
        }

        if (userMessage != null) {
            prompt.append("\n사용자: ")
                  .append(userMessage);
            prompt.append("\n당신: ");
        }

        return prompt;
    }

    public CompletableFuture<ApiResponse<String>> chatBotConversation(ChatBotRequest request) {

        String prompt = buildChatBotPrompt(request.getChatBotHistory(), request.getUserMessage());

        log.info("프롬프트 {}", prompt);

        CompletableFuture<String> future = new CompletableFuture<>();
        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                return chatBotAiService.chat(prompt);
            }, future)
        );

        // future가 완료될 때 ApiResponse로 변환
        return future.thenApply(ApiResponse::success);
    }

    public CompletableFuture<RecommendContentResponse> chatBotRecommendContent(
        RecommendRequest request, Authentication authentication
    ) {
        String email = authentication.getName();
        Optional<ChatBot> chatBot = findChatBotSummary(email);

        String summary = chatBot.map(ChatBot::getContentSummary).orElse(null);
        String agePrompt = userService.getUserAgePromptByEmail(email);
        String address = Optional.ofNullable(request.getAddress()).orElse("");

        StartAndDuringDTO userTime = getTime(request.getStartTime(), request.getEndTime());
        String prompt = getPrompt(summary, agePrompt, address);

        log.info("📩 프롬프트: {}", prompt);

        CompletableFuture<RecommendContentDTO> contentFuture = new CompletableFuture<>();

        EmbeddingStoreContentRetriever filteredRetriever = getContentDocument(userTime.startDate(), userTime.endDate());

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                return dynamicService.chatBotRecommendContent(prompt, userTime.startDate(), userTime.endDate(), filteredRetriever);
            }, contentFuture)
        );

        return contentFuture.thenApply(contentDto -> {
            if (contentDto == null || contentDto.event() == null) {
                return RecommendContentResponse.builder().contents(List.of()).build();
            }

            List<RecommendDTO> recommendList = contentDto.event();
            Map<Long, String> reasonMap = recommendList.stream()
                                                       .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));

            List<Long> recommendIds = recommendList.stream()
                                                   .map(RecommendDTO::id)
                                                   .filter(Objects::nonNull)
                                                   .toList();

            log.info("🔍 추천 ID 목록: {}", recommendIds);

            List<ContentWithReasonDTO> recommendContents = contentService.findByIds(recommendIds);
            recommendContents.forEach(content -> content.setReason(reasonMap.get(content.getId())));



            return RecommendContentResponse.builder().contents(recommendContents).build();
        });
    }
}
