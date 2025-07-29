package com.grepp.funfun.app.domain.recommend.service;

import com.grepp.funfun.app.domain.content.document.ContentEmbedding;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.domain.recommend.dto.StartAndDuringDTO;
import com.grepp.funfun.app.domain.recommend.entity.ChatBot;
import com.grepp.funfun.app.domain.recommend.repository.ChatBotRepository;
import com.grepp.funfun.app.domain.recommend.repository.ContentEmbeddingRepository;
import com.grepp.funfun.app.domain.recommend.repository.GroupEmbeddingRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import dev.langchain4j.data.embedding.Embedding;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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
}
