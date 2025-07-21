package com.grepp.funfun.app.domain.group.controller;

import com.grepp.funfun.app.domain.group.dto.payload.GroupListResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupMyResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupRequest;
import com.grepp.funfun.app.domain.group.dto.payload.GroupResponse;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GroupApiController {

    private final GroupService groupService;

    // 모임 상세 조회
    @GetMapping("/{groupId}")
    @Operation(summary = "모임 상세 조회", description = "모임을 상세 조회합니다.")
    public ResponseEntity<ApiResponse<GroupResponse>> getGroup(@PathVariable Long groupId,
        Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(ApiResponse.success(groupService.get(groupId,userEmail)));
    }

    // 내가 리더인 모임 조회
    @GetMapping("/getLeaderMy")
    @Operation(summary = "내가 리더 역할인 모임 조회", description = "내가 리더 역할인 모임을 조회합니다.")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getLeaderMyGroups(
        Authentication authentication
    ){
        String userEmail = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(groupService.findMyLeaderGroups(userEmail)));
    }

    // 내가 속한 모임 조회
    @GetMapping("/getMy")
    @Operation(summary = "내가 속한 모임 조회", description = "내가 속한 모임을 조회합니다.")
    public ResponseEntity<ApiResponse<List<GroupMyResponse>>> getMyGroups(
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(groupService.findMyGroups(userEmail)));
    }


    @GetMapping("/search")
    @Operation(summary = "모임 검색 및 조회", description = """
            아래와 형식으로 입력해주세요.
            
            • category(null 허용)
            - ART, TRAVEL, FOOD,GAME,CULTURE,SPORT,STUDY,MOVIE
            
            • keyword(null 허용)
            - 검색을 원하는 키워드
           
            • sortBy
            - recent(최신순) , viewCount(조회수) , distance(거리순)
            - 처음 접속했을 때 모든 기준 초기값 : 거리순
            
            ex)
            ART / 모임 / recent
            아무것도 넣지 않고, 검색하면 거리순
            """
    )
    public ResponseEntity<ApiResponse<Page<GroupListResponse>>> searchGroups(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "distance") String sortBy,
        @PageableDefault(size = 10)
        @ParameterObject Pageable pageable,
    Authentication authentication
    ) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(ApiResponse.success(groupService.getGroups(category, keyword, sortBy, userEmail,pageable)));
    }

    // 모임 생성
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "모임 생성", description = "모임을 생성합니다.")
    public ResponseEntity<ApiResponse<String>> createGroup(@ModelAttribute @Valid GroupRequest request,
        Authentication authentication) {
        try {
            String leaderEmail = authentication.getName();
            groupService.create(leaderEmail, request);

            return ResponseEntity.ok(ApiResponse.success("모임이 성공적으로 생성되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }

    // 모임 수정
    @PutMapping(value = "/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "모임 수정", description = "모임을 수정합니다.")
    public ResponseEntity<ApiResponse<String>> updateGroup(@PathVariable Long groupId,
        @ModelAttribute @Valid GroupRequest updateRequest, Authentication authentication) {
        try {
            String leaderEmail = authentication.getName();
            groupService.update(groupId, leaderEmail, updateRequest);

            return ResponseEntity.ok(ApiResponse.success("모임이 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }

    // 모임 삭제
    @DeleteMapping("{groupId}")
    @Operation(summary = "모임 삭제", description = "모임을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteGroup(@PathVariable Long groupId,
        Authentication authentication) {
        try {
            String leaderEmail = authentication.getName();
            groupService.delete(groupId, leaderEmail);

            return ResponseEntity.ok(ApiResponse.success("모임이 성공적으로 삭제되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));

        }
    }

    // 모임 취소
    @PostMapping("/{groupId}/cancel")
    @Operation(summary = "모임 취소", description = "모임을 취소합니다.")
    public ResponseEntity<ApiResponse<String>> cancelGroup(
        @PathVariable Long groupId,
        Authentication authentication) {
        try {
            String leaderEmail = authentication.getName();
            groupService.cancel(groupId, leaderEmail);

            return ResponseEntity.ok(ApiResponse.success("모임이 성공적으로 취소되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }

    // 모임 완료
    @PostMapping("/{groupId}/complete")
    @Operation(summary = "모임 완료", description = "모임을 완료 처리합니다.")
    public ResponseEntity<ApiResponse<String>> completeGroup(
        @PathVariable Long groupId,
        Authentication authentication) {
        try {
            String leaderEmail = authentication.getName();
            groupService.complete(groupId, leaderEmail);

            return ResponseEntity.ok(ApiResponse.success("모임이 완료 처리 되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(ResponseCode.BAD_REQUEST, e.getMessage()));
        }
    }
}

