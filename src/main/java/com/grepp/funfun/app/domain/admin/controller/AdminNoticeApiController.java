package com.grepp.funfun.app.domain.admin.controller;

import com.grepp.funfun.app.domain.admin.dto.AdminNoticeDTO;
import com.grepp.funfun.app.domain.admin.service.AdminNoticeService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notices")
public class AdminNoticeApiController {

    private final AdminNoticeService adminNoticeService;

    @PostMapping
    @Operation(summary = "공지사항 등록 및 전체 알림 전송", description = "관리자가 공지사항을 작성하고, 전체 사용자에게 알림을 발송합니다.")
    public ResponseEntity<ApiResponse<Long>> createNotice(@RequestBody @Valid AdminNoticeDTO dto) {
        Long noticeId = adminNoticeService.create(dto);
        return ResponseEntity.ok(ApiResponse.success(noticeId));
    }

    @GetMapping
    @Operation(summary = "공지사항 전체 조회", description = "공지사항 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<AdminNoticeDTO>>> findAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminNoticeService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항의 상세 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<AdminNoticeDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminNoticeService.findById(id)));
    }
}
