package com.grepp.funfun.app.domain.recommend.controller;

import com.grepp.funfun.app.domain.content.dto.ContentWithReasonDTO;
import com.grepp.funfun.app.domain.content.service.ContentService;
import com.grepp.funfun.app.domain.group.dto.GroupWithReasonDTO;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.domain.recommend.dto.RecommendContentDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendDTO;
import com.grepp.funfun.app.domain.recommend.dto.RecommendGroupDTO;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendGroupResponse;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendRequest;
import com.grepp.funfun.app.domain.recommend.dto.payload.RecommendTwoListResponse;
import com.grepp.funfun.app.domain.recommend.service.ChatBotService;
import com.grepp.funfun.app.domain.recommend.service.ContentAiService;
import com.grepp.funfun.app.domain.recommend.service.GroupAiService;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/chatBots", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatBotApiController {

    private final ChatBotService chatBotService;
    private final ContentAiService contentAiService;
    private final GroupAiService groupAiService;
    private final ContentService contentService;
    private final GroupService groupService;
    private final UserService userService;

    @PostMapping("chat")
    public String chat(@RequestBody String message) {
        return contentAiService.chat(message);
    }

    @PostMapping("content")
    public ResponseEntity<ApiResponse<RecommendTwoListResponse>> quickRecommendContent(
        @RequestBody RecommendRequest request, Authentication authentication) {
//        String email = authentication.getName();
//        String kind = request.getEventType().toString();
//        String preference = userService.getUserPreferenceDescription(email, kind);
        String prompt = request.getStartTime() + "부터 " + request.getEndTime()
            + "까지 여가시간인데 이 때 할만한활동을 추천해주는데 "
            //+ preference
            + "내가 선호하는 활동을 고려해서 장소, 시간 조건에 맞게 실제로 실행할 수 있는 활동들을 추천해줘";
        RecommendContentDTO contents = contentAiService.recommendContent(prompt);
        List<RecommendDTO> recommendList = contents.event();


        String promptForPlace = request.getAddress() + "근처에서 할만한 활동을 추천해줘";
        RecommendContentDTO place = contentAiService.recommendPlace(promptForPlace);  // 장소 추천
        List<RecommendDTO> recommendPlaceList = place.event();

        Map<Long, String> reasonMap = recommendList.stream()
                                                   .filter(dto -> dto.id()
                                                       != null) // id가 null인 DTO는 건너뛰기
                                                   .collect(Collectors.toMap(
                                                       dto -> Long.valueOf(dto.id()),
                                                       // String id를 Long으로 변환
                                                       RecommendDTO::reason,
                                                       // reason 필드를 값으로 사용
                                                       (existingValue, newValue) -> existingValue
                                                       // 동일한 id가 여러 개 있을 경우, 기존 값 유지
                                                   ));

        Map<Long, String> reasonPlaceMap = recommendPlaceList.stream()
                                                   .filter(dto -> dto.id()
                                                       != null)
                                                   .collect(Collectors.toMap(
                                                       dto -> Long.valueOf(dto.id()),
                                                       RecommendDTO::reason,
                                                       (existingValue, newValue) -> existingValue
                                                   ));
        // id로 데이터 찾기 위함
        List<Long> recommendIds = recommendList.stream()
                                               .map(RecommendDTO::id)
                                               .map(Long::valueOf)
                                               .filter(java.util.Objects::nonNull)
                                               .toList();

        List<Long> recommendPlaceIds = recommendPlaceList.stream()
                                               .map(RecommendDTO::id)
                                               .map(Long::valueOf)
                                               .filter(java.util.Objects::nonNull)
                                               .toList();

        log.info(" 추천 결과 ==================");
        for (RecommendDTO dto : recommendList) {
            log.info("id: {}", dto.id());
            log.info("title: {}", dto.title());
        }

        log.info(" 장소데이터 ==================");
        for (RecommendDTO dto : recommendPlaceList) {
            log.info("id: {}", dto.id());
            log.info("title: {}", dto.title());
        }

        List<ContentWithReasonDTO> recommendContents = contentService.findByIds(recommendIds);

        recommendContents.forEach(content ->
                                      content.setReason(reasonMap.get(content.getId()))
        );


        List<ContentWithReasonDTO> recommendPlaces = contentService.findByIds(recommendPlaceIds);

        recommendPlaces.forEach(content ->
                                    content.setReason(reasonPlaceMap.get(content.getId()))
        );

        RecommendTwoListResponse finalResponse = RecommendTwoListResponse.builder()
                                                                                         .events(recommendContents)
                                                                                         .places(recommendPlaces)
                                                                                         .build();

        return ResponseEntity.ok(ApiResponse.success(finalResponse));

    }

    @PostMapping("group")
    public ResponseEntity<ApiResponse<RecommendGroupResponse>> quickRecommendGroup(
        @RequestBody RecommendRequest request,
        Authentication authentication) {
//        String email = authentication.getName();
//        String kind = request.getEventType().toString();
//        String preference = userService.getUserPreferenceDescription(email, kind);

        String prompt = request.getStartTime() + "부터 " + request.getEndTime()
            + "까지 여가시간인데 이 때 할만한활동을 추천해주는데 "
            //+ preference
            + "내가 선호하는 활동을 고려해서 장소, 시간 조건에 맞게 실제로 실행할 수 있는 활동들을 추천해줘";
        RecommendGroupDTO groups = groupAiService.recommend(prompt);
        List<RecommendDTO> recommendList = groups.group();

        Map<Long, String> reasonMap = recommendList.stream()
                                                   .filter(dto -> dto.id()
                                                       != null) // id가 null인 DTO는 건너뛰기
                                                   .collect(Collectors.toMap(
                                                       dto -> Long.valueOf(dto.id()),
                                                       // String id를 Long으로 변환
                                                       RecommendDTO::reason,
                                                       // reason 필드를 값으로 사용
                                                       (existingValue, newValue) -> existingValue
                                                       // 동일한 id가 여러 개 있을 경우, 기존 값 유지
                                                   ));

        List<Long> recommendIds = recommendList.stream()
                                               .map(RecommendDTO::id)
                                               .map(Long::valueOf)
                                               .filter(java.util.Objects::nonNull)
                                               .toList();

        log.info(" 추천 결과 ==================");
        for (RecommendDTO dto : recommendList) {
            log.info("id: {}", dto.id());
            log.info("title: {}", dto.title());
        }

        List<GroupWithReasonDTO> recommendGroups = groupService.findByIds(recommendIds);

        recommendGroups.forEach(content ->
                                      content.setReason(reasonMap.get(content.getId()))
        );

        RecommendGroupResponse finalResponse = RecommendGroupResponse.builder()
                                                                       .groups(recommendGroups)
                                                                       .build();

        return ResponseEntity.ok(ApiResponse.success(finalResponse));

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
