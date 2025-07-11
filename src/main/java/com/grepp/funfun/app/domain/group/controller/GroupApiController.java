package com.grepp.funfun.app.domain.group.controller;

import com.grepp.funfun.app.domain.group.dto.payload.GroupMyResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupRequest;
import com.grepp.funfun.app.domain.group.dto.payload.GroupResponse;
import com.grepp.funfun.app.domain.group.dto.GroupDTO;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/groups", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class GroupApiController {

    private final GroupService groupService;

    // 특정 모임 조회(상세)
    @GetMapping("/{id}")
    @Operation(summary = "특정 모임 조회", description = "특정 모임을 조회합니다.")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(groupService.get(id));
    }

    // 모든 모임 조회(최신순)
    @GetMapping
    @Operation(summary = "모든 모임 조회(최신순)", description = "모든 모임을 조회합니다.(최신순)")
    public ResponseEntity<List<GroupResponse>> getAllRecentGroups() {
        return ResponseEntity.ok(groupService.getRecentGroups());
    }

    // 내가 속한 모임 조회
    @GetMapping("/my")
    @Operation(summary = "내가 속한 모임 조회", description = "내가 속한 모임을 조회합니다.")
    public ResponseEntity<List<GroupMyResponse>> getMyGroups(
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(groupService.findMyGroups(userEmail));
    }

    // 모임 생성
    @PostMapping("/create")
    @Operation(summary = "모임 생성", description = "모임을 생성합니다.")
    public ResponseEntity<ApiResponse<String>> createGroup(@RequestBody @Valid GroupRequest request,
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
    @PutMapping("{groupId}")
    @Operation(summary = "모임 수정", description = "모임을 수정합니다.")
    public ResponseEntity<ApiResponse<String>> updateGroup(@PathVariable Long groupId,
        @RequestBody GroupRequest updateRequest, Authentication authentication) {
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

