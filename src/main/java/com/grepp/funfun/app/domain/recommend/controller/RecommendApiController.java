package com.grepp.funfun.app.domain.recommend.controller;

import com.grepp.funfun.app.domain.content.dto.ContentWithReasonDTO;
import com.grepp.funfun.app.domain.content.service.ContentService;
import com.grepp.funfun.app.domain.group.dto.GroupWithReasonDTO;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.domain.recommend.dto.RecommendContentDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendGroupDTO;
import com.grepp.funfun.app.domain.recommend.dto.payload.ChatBotMessage;
import com.grepp.funfun.app.domain.recommend.dto.payload.ChatBotRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendContentResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendGroupResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendTwoListResponse;
import com.grepp.funfun.app.domain.recommend.entity.ChatBot;
import com.grepp.funfun.app.domain.recommend.service.AiRequestQueue;
import com.grepp.funfun.app.domain.recommend.service.ChatBotAiService;
import com.grepp.funfun.app.domain.recommend.service.ChatBotService;
import com.grepp.funfun.app.domain.recommend.service.ContentAiService;
import com.grepp.funfun.app.domain.recommend.service.GroupAiService;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import dev.langchain4j.service.output.OutputParsingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI 추천 API", description = "추천과 관련된 기능들입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecommendApiController {

    private final ChatBotService chatBotService;
    private final ContentAiService contentAiService;
    private final GroupAiService groupAiService;
    private final ContentService contentService;
    private final GroupService groupService;
    private final UserService userService;
    private final AiRequestQueue aiRequestQueue;
    private final ChatBotAiService chatBotAiService;

    @PostMapping("chatBot/chat")
    @Operation(summary = "챗봇 대화 기능", description = "챗봇과 대화하여 사용자의 취향을 분석함")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> chat(
        @RequestBody ChatBotRequest request) {
        String prompt = buildChatBotPrompt(request.getChatBotHistory(), request.getUserMessage());

        log.info("프롬프트 {}", prompt);

        CompletableFuture<String> future = new CompletableFuture<>();
        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                return chatBotAiService.chat(prompt);
                // return ApiResponse.success(response);
            }, future)
        );

        // future가 완료될 때 ApiResponse로 변환
        return future.thenApply(result ->
                                    ResponseEntity.ok(ApiResponse.success(result))
        );
    }

    @PostMapping("chatBot/end")
    @Operation(summary = "챗봇 대화내용 요약 기능", description = "챗봇과의 대화내용을 바탕으로 사용자의 취향을 분석하여 저장")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> chatBotCloseAndSummary(
        @RequestBody ChatBotRequest request, Authentication authentication) {
        String prompt = buildChatBotPrompt(request.getChatBotHistory(), null);
        String email = authentication.getName();
        Optional<ChatBot> chatBot = chatBotService.findChatBotSummary(email);
        log.info("최종 대화 내역: {}", prompt);
        CompletableFuture<String> future = new CompletableFuture<>();
        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                String summary = chatBotAiService.summary(prompt);
                log.info("취향 요약: {}", summary);
                if (chatBot.isPresent()) {
                    chatBotService.updateSummary(chatBot.get()
                                                        .getId(), summary);
                } else {
                    if(request.getEventType().equals("CONTENT")){
                        chatBotService.registSummary(ChatBot.builder()
                                                            .email(email)
                                                            .contentSummary(summary)
                                                            .build());
                    } else if(request.getEventType().equals("GROUP")){
                        chatBotService.registSummary(ChatBot.builder()
                                                            .email(email)
                                                            .groupSummary(summary)
                                                            .build());
                    }
                }
                return summary;
            }, future)
        );
        return future.thenApply(result ->
                                    ResponseEntity.ok(ApiResponse.success(result))
        );
    }

    @PostMapping("chatBot/recommend/content")
    @Operation(summary = "챗봇 추천 기능 (컨텐츠)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public CompletableFuture<ResponseEntity<ApiResponse<RecommendContentResponse>>> chatBotRecommendContent(
        @RequestBody RecommendRequest request, Authentication authentication
    ){
        String email = authentication.getName();
        Optional<ChatBot> chatBot = chatBotService.findChatBotSummary(email);
        String summary = null;
        if (chatBot.isPresent()) {
            summary = chatBot.get()
                                    .getContentSummary();
        }

        String agePrompt = userService.getUserAgePromptByEmail(email);

        String datePrompt = ChatBotService.getDate(request.getStartTime(),
                                                   request.getEndTime()); // 여가시간

        String prompt =
            agePrompt + datePrompt + "나는 지금 " + request.getAddress() + "에 있어"
                + summary
                + " 이건 나의 취향을 분석한 후 요약한 내용인데 이걸 고려해서 "
                + " 장소, 여가시간 맞게 실제로 실행할 수 있는 활동들을 추천해줘";

        CompletableFuture<RecommendContentDTO> contentFuture = new CompletableFuture<>();

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                return contentAiService.chatBotRecommendContent(prompt);
            }, contentFuture)
        );

        return contentFuture
            .thenApplyAsync(contents -> {
                List<RecommendDTO> recommendList = contents.event();
                // 추천 이유 맵 생성
                Map<Long, String> reasonMap = recommendList.stream()
                                                           .collect(
                                                               Collectors.toMap(RecommendDTO::id,
                                                                                RecommendDTO::reason));

                // ID 리스트 생성
                List<Long> recommendIds = recommendList.stream()
                                                       .map(RecommendDTO::id)
                                                       .filter(Objects::nonNull)
                                                       .toList();

                log.info("모임 추천 결과 ==================");
                for (RecommendDTO dto : recommendList) {
                    log.info("id: {}", dto.id());
                    log.info("추천이유 : {}", dto.reason());
                }

                List<ContentWithReasonDTO> recommendContents= contentService.findByIds(recommendIds);
                recommendContents.forEach(content ->
                                            content.setReason(reasonMap.get(
                                                content.getId()))
                );

                RecommendContentResponse finalResponse = RecommendContentResponse.builder()
                                                                             .contents(
                                                                                 recommendContents)
                                                                             .build();
                return ResponseEntity.ok(ApiResponse.success(finalResponse));

            })
            .exceptionally(e -> {
                log.error("API 호출 중 오류 발생: " + e.getMessage(), e);
                Throwable cause = e.getCause();
                if (cause instanceof OutputParsingException) {
                    return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(ApiResponse.error(ResponseCode.INVALID_API_RESPONSE, "AI 응답이 길어서 Json 파싱에서 문제 발생"));
                } else if (cause instanceof SocketTimeoutException) {
                    return ResponseEntity
                        .status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(ApiResponse.error(ResponseCode.API_UNAVAILABLE,
                                                "AI 서버 응답이 지연되고 있습니다. 잠시 후 다시 시도해 주세요."));

                } else {
                    return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                "요청 처리 중 알 수 없는 오류가 발생했습니다."));
                }
            });
    }

    @PostMapping("chatBot/recommend/group")
    @Operation(summary = "챗봇 추천 기능 (모임)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public CompletableFuture<ResponseEntity<ApiResponse<RecommendGroupResponse>>> chatBotRecommendGroup(
        @RequestBody RecommendRequest request, Authentication authentication
    ){
        String email = authentication.getName();
        Optional<ChatBot> chatBot = chatBotService.findChatBotSummary(email);
        String summary = null;
        if (chatBot.isPresent()) {
            summary = chatBot.get()
                             .getGroupSummary();
        }

        String agePrompt = userService.getUserAgePromptByEmail(email);

        String datePrompt = ChatBotService.getDate(request.getStartTime(),
                                                   request.getEndTime()); // 여가시간

        String prompt =
            agePrompt + datePrompt + "나는 지금 " + request.getAddress() + "에 있어"
                + summary
                + " 이건 나의 취향을 분석한 후 요약한 내용인데 이걸 고려해서 "
                + " 장소, 여가시간 맞게 실제로 실행할 수 있는 활동들을 추천해줘";

        log.info("작성 프롬프트: {}", prompt);

        CompletableFuture<RecommendGroupDTO> groupFuture = new CompletableFuture<>();

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                RecommendGroupDTO groups = groupAiService.chatBotRecommendGroup(prompt);
                return groups;
            }, groupFuture)
        );

        return groupFuture
            .thenApplyAsync(groups -> {
                List<RecommendDTO> recommendList = groups.group();
                // 추천 이유 맵 생성
                Map<Long, String> reasonMap = recommendList.stream()
                                                           .collect(
                                                               Collectors.toMap(RecommendDTO::id,
                                                                                RecommendDTO::reason));

                // ID 리스트 생성
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
                                            content.setReason(reasonMap.get(
                                                content.getId()))
                );

                RecommendGroupResponse finalResponse = RecommendGroupResponse.builder()
                                                                             .groups(
                                                                                 recommendGroups)
                                                                             .build();
                return ResponseEntity.ok(ApiResponse.success(finalResponse));

            })
            .exceptionally(e -> {
                log.error("API 호출 중 오류 발생: " + e.getMessage(), e);

                // CompletableFuture가 전달하는 예외는 ExecutionException으로 감싸져 있으므로,
                // 실제 원인(cause)을 확인해야 함
                Throwable cause = e.getCause();

                // 1. JSON 파싱 오류인지 확인
                if (cause instanceof OutputParsingException) {
                    return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(ApiResponse.error(ResponseCode.INVALID_API_RESPONSE, "AI 응답이 길어서 Json 파싱에서 문제 발생"));
                    // 2. 소켓 타임아웃 오류인지 확인 (서버 과부하 가능성)
                } else if (cause instanceof SocketTimeoutException) {
                    return ResponseEntity
                        .status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(ApiResponse.error(ResponseCode.API_UNAVAILABLE,
                                                "AI 서버 응답이 지연되고 있습니다. 잠시 후 다시 시도해 주세요."));

                    // 3. 그 외 알 수 없는 모든 오류
                } else {
                    return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                "요청 처리 중 알 수 없는 오류가 발생했습니다."));
                }
            });
    }

    @PostMapping("recommend/content")
    @Operation(summary = "AI 빠른추천 기능 (컨텐츠)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public CompletableFuture<ResponseEntity<ApiResponse<RecommendTwoListResponse>>> quickRecommendContent(
        @RequestBody RecommendRequest request, Authentication authentication) {
        String email = authentication.getName();
        String preference = userService.getUserPreferenceDescription(email, "CONTENT");
        String agePrompt = userService.getUserAgePromptByEmail(email);

        String datePrompt = ChatBotService.getDate(request.getStartTime(),
                                                   request.getEndTime()); // 여가시간

        String prompt =
            agePrompt + datePrompt + preference + "나는 지금 " + request.getAddress() + "에 있어"
                + " 내가 선호하는 활동을 고려해서 장소, 여가시간 맞게 실제로 실행할 수 있는 활동들을 추천해줘";

        String promptForPlace = request.getAddress() + "근처에서 할만한 활동을 추천해줘";
        log.info("사용자 프롬프트: {}", prompt);

        CompletableFuture<RecommendContentDTO> contentFuture = new CompletableFuture<>();
        CompletableFuture<RecommendContentDTO> placeFuture = new CompletableFuture<>();

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                return contentAiService.recommendContent(prompt);
            }, contentFuture)
        );

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                return contentAiService.recommendPlace(promptForPlace);
            }, placeFuture)
        );

//        return ResponseEntity.ok(ApiResponse.success(finalResponse));
        return contentFuture
            // contentFuture가 완료되면 컨텐츠 처리 로직 연결
            .thenApplyAsync(contents -> {
                List<RecommendDTO> recommendList = contents.event();
                Map<Long, String> reasonMap = recommendList.stream()
                                                           .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));
                List<Long> recommendIds = recommendList.stream()
                                                       .map(RecommendDTO::id)
                                                       .filter(Objects::nonNull)
                                                       .toList();

                log.info("컨텐츠 추천 결과 ==================");
                recommendList.forEach(dto -> log.info("id: {}, 추천이유 : {}", dto.id(), dto.reason()));

                List<ContentWithReasonDTO> recommendContents = contentService.findByIds(recommendIds);
                recommendContents.forEach(content -> content.setReason(reasonMap.get(content.getId())));
                return recommendContents;
            })
            // contentFuture의 후속 작업과 placeFuture를 병합
            // placeFuture도 contentFuture와 동시에 실행됨
            .thenCombineAsync(placeFuture.thenApplyAsync(place -> {
                List<RecommendDTO> recommendPlaceList = place.event();
                Map<Long, String> reasonPlaceMap = recommendPlaceList.stream()
                                                                     .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));
                List<Long> recommendPlaceIds = recommendPlaceList.stream()
                                                                 .map(RecommendDTO::id)
                                                                 .filter(Objects::nonNull)
                                                                 .toList();

                log.info("장소 추천 결과 ==================");
                recommendPlaceList.forEach(dto -> log.info("id: {}, 추천이유 : {}", dto.id(), dto.reason()));

                List<ContentWithReasonDTO> recommendPlaces = contentService.findByIds(recommendPlaceIds);
                recommendPlaces.forEach(content -> content.setReason(reasonPlaceMap.get(content.getId())));
                return recommendPlaces;
            }), (recommendContents, recommendPlaces) -> {
                // 두 작업이 모두 완료되면 결과를 Response에 묶어서 반환
                RecommendTwoListResponse finalResponse = RecommendTwoListResponse.builder()
                                                                                 .events(recommendContents)
                                                                                 .places(recommendPlaces)
                                                                                 .build();
                return ResponseEntity.ok(ApiResponse.success(finalResponse));
            })
            .exceptionally(e -> {
                log.error("API 호출 중 오류 발생: " + e.getMessage(), e);

                Throwable cause = e.getCause();

                if (cause instanceof OutputParsingException) {
                    return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(ApiResponse.error(ResponseCode.INVALID_API_RESPONSE, "AI 응답이 길어서 Json 파싱에서 문제 발생"));
                } else if (cause instanceof SocketTimeoutException) {
                    return ResponseEntity
                        .status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(ApiResponse.error(ResponseCode.API_UNAVAILABLE, "AI 서버 응답이 지연되고 있습니다. 잠시 후 다시 시도해 주세요."));
                } else {
                    return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "요청 처리 중 알 수 없는 오류가 발생했습니다."));
                }
            });

//        RecommendContentDTO contents = contentAiService.recommendContent(prompt);

//        RecommendContentDTO place = contentAiService.recommendPlace(promptForPlace);  // 장소 추천

//        List<RecommendDTO> recommendList = contents.event();
//        List<RecommendDTO> recommendPlaceList = place.event();
//
//        Map<Long, String> reasonMap = recommendList.stream()
//                                                     .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));
//
//        Map<Long, String> reasonPlaceMap = recommendPlaceList.stream()
//                                                     .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));
//        // id로 데이터 찾기 위함
//        List<Long> recommendIds = recommendList.stream()
//                                               .map(RecommendDTO::id)
//                                               .map(Long::valueOf)
//                                               .filter(Objects::nonNull)
//                                               .toList();
//
//        List<Long> recommendPlaceIds = recommendPlaceList.stream()
//                                               .map(RecommendDTO::id)
//                                               .map(Long::valueOf)
//                                               .filter(Objects::nonNull)
//                                               .toList();
//
//        log.info("컨텐츠 추천 결과 ==================");
//        for (RecommendDTO dto : recommendList) {
//            log.info("id: {}", dto.id());
//            log.info("추천이유 : {}", dto.reason());
//        }
//
//        log.info("장소 추천 결과 ==================");
//        for (RecommendDTO dto : recommendPlaceList) {
//            log.info("id: {}", dto.id());
//            log.info("추천이유 : {}", dto.reason());
//        }
//
//        List<ContentWithReasonDTO> recommendContents = contentService.findByIds(recommendIds);
//
//        recommendContents.forEach(content ->
//                                      content.setReason(reasonMap.get(content.getId()))
//        );
//
//
//        List<ContentWithReasonDTO> recommendPlaces = contentService.findByIds(recommendPlaceIds);
//
//        recommendPlaces.forEach(content ->
//                                    content.setReason(reasonPlaceMap.get(content.getId()))
//        );
//
//        RecommendTwoListResponse finalResponse = RecommendTwoListResponse.builder()
//                                                                                         .events(recommendContents)
//                                                                                         .places(recommendPlaces)
//                                                                                         .build();
    }

    @PostMapping("recommend/group")
    @Operation(summary = "AI 빠른추천 기능 (모임)", description = "시간, 장소를 입력하여 추천을 받습니다.")
    public CompletableFuture<ResponseEntity<ApiResponse<RecommendGroupResponse>>> quickRecommendGroup(
        @RequestBody RecommendRequest request,
        Authentication authentication) {
        String email = authentication.getName();
        String preference = userService.getUserPreferenceDescription(email, "GROUP");

        String prompt = request.getStartTime() + "부터 " + request.getEndTime()
            + "까지 여가시간이고 나는 " + request.getAddress()
            + " 주변에서 할만한 활동을 추천받고 싶어 "
            + preference
            + "내가 선호하는 활동을 고려해서 시간과 장소에 맞게 실제로 실행할 수 있는 활동들을 추천해줘";
        log.info("사용자 프롬프트 {}", prompt);

        CompletableFuture<RecommendGroupDTO> groupFuture = new CompletableFuture<>();

        aiRequestQueue.addRequest(
            new AiRequestQueue.AiRequestTask<>(() -> {
                RecommendGroupDTO groups = groupAiService.recommend(prompt);
                return groups;
            }, groupFuture)
        );

        return groupFuture
            .thenApplyAsync(groups -> {
                List<RecommendDTO> recommendList = groups.group();
                // 추천 이유 맵 생성
                Map<Long, String> reasonMap = recommendList.stream()
                                                           .collect(
                                                               Collectors.toMap(RecommendDTO::id,
                                                                                RecommendDTO::reason));

                // ID 리스트 생성
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
                                            content.setReason(reasonMap.get(
                                                content.getId()))
                );

                RecommendGroupResponse finalResponse = RecommendGroupResponse.builder()
                                                                             .groups(
                                                                                 recommendGroups)
                                                                             .build();
                return ResponseEntity.ok(ApiResponse.success(finalResponse));

            })
            .exceptionally(e -> {
                log.error("API 호출 중 오류 발생: " + e.getMessage(), e);

                // CompletableFuture가 전달하는 예외는 ExecutionException으로 감싸져 있으므로,
                // 실제 원인(cause)을 확인해야 함
                Throwable cause = e.getCause();

                // 1. JSON 파싱 오류인지 확인
                if (cause instanceof OutputParsingException) {
                    return ResponseEntity
                        .status(HttpStatus.BAD_GATEWAY)
                        .body(ApiResponse.error(ResponseCode.INVALID_API_RESPONSE, "AI 응답이 길어서 Json 파싱에서 문제 발생"));
                    // 2. 소켓 타임아웃 오류인지 확인 (서버 과부하 가능성)
                } else if (cause instanceof SocketTimeoutException) {
                    return ResponseEntity
                        .status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(ApiResponse.error(ResponseCode.API_UNAVAILABLE,
                                                "AI 서버 응답이 지연되고 있습니다. 잠시 후 다시 시도해 주세요."));

                    // 3. 그 외 알 수 없는 모든 오류
                } else {
                    return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR,
                                                "요청 처리 중 알 수 없는 오류가 발생했습니다."));
                }
            });
//        RecommendGroupDTO groups = groupAiService.recommend(prompt);
//        List<RecommendDTO> recommendList = groups.group();
//
////        Map<Long, String> reasonMap = recommendList.stream()
////                                                   .filter(dto -> dto.id()
////                                                       != null) // id가 null인 DTO는 건너뛰기
////                                                   .collect(Collectors.toMap(
////                                                       dto -> Long.valueOf(dto.id()),
////                                                       // String id를 Long으로 변환
////                                                       RecommendDTO::reason,
////                                                       // reason 필드를 값으로 사용
////                                                       (existingValue, newValue) -> existingValue
////                                                       // 동일한 id가 여러 개 있을 경우, 기존 값 유지
////                                                   ));
//
//        Map<Long, String> reasonMap = recommendList.stream()
//                                                             .collect(Collectors.toMap(RecommendDTO::id, RecommendDTO::reason));
//
//        List<Long> recommendIds = recommendList.stream()
//                                               .map(RecommendDTO::id)
//                                               .filter(java.util.Objects::nonNull)
//                                               .toList();
//
//        log.info("모임 추천 결과 ==================");
//        for (RecommendDTO dto : recommendList) {
//            log.info("id: {}", dto.id());
//            log.info("추천이유 : {}", dto.reason());
//        }
//
//        List<GroupWithReasonDTO> recommendGroups = groupService.findByIds(recommendIds);
//
//        recommendGroups.forEach(content ->
//                                      content.setReason(reasonMap.get(content.getId()))
//        );
//
//        RecommendGroupResponse finalResponse = RecommendGroupResponse.builder()
//                                                                       .groups(recommendGroups)
//                                                                       .build();
//
//        return ResponseEntity.ok(ApiResponse.success(finalResponse));

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

//    @GetMapping
//    public ResponseEntity<List<ChatBotDTO>> getAllChatBots() {
//        return ResponseEntity.ok(chatBotService.findAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ChatBotDTO> getChatBot(@PathVariable(name = "id") final Long id) {
//        return ResponseEntity.ok(chatBotService.get(id));
//    }
//
//    @PostMapping
//    @ApiResponse(responseCode = "201")
//    public ResponseEntity<Long> createChatBot(@RequestBody @Valid final ChatBotDTO chatBotDTO) {
//        final Long createdId = chatBotService.create(chatBotDTO);
//        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Long> updateChatBot(@PathVariable(name = "id") final Long id,
//        @RequestBody @Valid final ChatBotDTO chatBotDTO) {
//        chatBotService.update(id, chatBotDTO);
//        return ResponseEntity.ok(id);
//    }
//
//    @DeleteMapping("/{id}")
//    @ApiResponse(responseCode = "204")
//    public ResponseEntity<Void> deleteChatBot(@PathVariable(name = "id") final Long id) {
//        chatBotService.delete(id);
//        return ResponseEntity.noContent()
//                             .build();
//    }

}
