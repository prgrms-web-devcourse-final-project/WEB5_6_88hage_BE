package com.grepp.funfun.app.domain.admin.controller;

import com.grepp.funfun.app.domain.admin.service.AdminGroupService;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.domain.admin.dto.payload.AdminGroupResponse;
import com.grepp.funfun.app.domain.admin.mapper.AdminGroupMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/groups")
public class AdminGroupApiController {

    private final AdminGroupService adminGroupService;
    private final AdminGroupMapper adminGroupMapper;

    // 전체 or 상태별 조회
    @Operation(summary = "관리자 - 모임 상태별 조회", description = "모임을 Status(RECRUITING, DELETE 등) 기준으로 필터링하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AdminGroupResponse>>> getGroupsByStatus(
            @RequestParam(required = false) GroupStatus status,
            @ParameterObject Pageable pageable
    ) {
        Page<Group> groups = adminGroupService.getGroupsByStatus(status, pageable);
        Page<AdminGroupResponse> response = groups.map(adminGroupMapper::toDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 관리자 게시글 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<String>> deleteGroupByAdmin(
            @PathVariable Long groupId,
            @RequestParam String reason,
            Authentication authentication
    ) {
        String adminEmail = authentication.getName();
        adminGroupService.deleteGroupByAdmin(groupId, adminEmail, reason);
        return ResponseEntity.ok(ApiResponse.success("삭제 완료"));
    }
}
