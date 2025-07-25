package com.grepp.funfun.app.domain.admin.controller;

import com.grepp.funfun.app.domain.admin.dto.payload.AdminContactAnswerRequest;
import com.grepp.funfun.app.domain.admin.dto.payload.AdminContactCategoryResponse;
import com.grepp.funfun.app.domain.admin.dto.payload.AdminContactStatusResponse;
import com.grepp.funfun.app.domain.admin.service.AdminContactAnswerService;
import com.grepp.funfun.app.domain.admin.service.AdminContactQueryService;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactDetailResponse;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactResponse;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/contacts")
@RequiredArgsConstructor
public class AdminContactApiController {

    private final AdminContactAnswerService adminContactAnswerService;
    private final AdminContactQueryService adminContactQueryService;


    @PostMapping("/{contactId}/answer")
    @Operation(summary = "문의 답변 작성", description = "관리자가 사용자의 문의에 답변을 등록합니다.")
    public ResponseEntity<ApiResponse<String>> answerContact(
            @PathVariable Long contactId,
            @RequestBody @Valid AdminContactAnswerRequest adminContactAnswerRequest) {
        adminContactAnswerService.answerContact(contactId, adminContactAnswerRequest.getAnswer());
        return ResponseEntity.ok(ApiResponse.success("답변이 등록되었습니다."));
    }

    @GetMapping
    @Operation(summary = "문의 전체 목록 조회 (관리자)", description = "모든 문의를 상태별로 조회합니다.")
    public ResponseEntity<ApiResponse<Page<ContactResponse>>> getAllContactsForAdmin(
            @RequestParam(defaultValue = "all") String status,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ContactResponse> contacts = adminContactQueryService.findAll(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }

    @GetMapping("/statuses")
    @Operation(summary = "문의 상태 목록", description = "문의 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AdminContactStatusResponse>>> getStatuses() {
        return ResponseEntity.ok(ApiResponse.success(adminContactQueryService.getAvailableStatuses()));
    }

    @GetMapping("/{contactId}")
    @Operation(summary = "문의 상세 조회 (관리자)", description = "각각의 문의 상세 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<ContactDetailResponse>> getContactDetail(
            @PathVariable Long contactId) {
        ContactDetailResponse response = adminContactQueryService.getDetail(contactId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categories")
    @Operation(summary = "문의 카테고리 목록", description = "문의 카테고리를 조회합니다.")
    public ResponseEntity<ApiResponse<List<AdminContactCategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(adminContactQueryService.getAvailableCategories()));
    }
}
