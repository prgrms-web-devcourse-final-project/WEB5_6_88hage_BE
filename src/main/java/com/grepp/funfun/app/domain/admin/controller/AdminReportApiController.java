package com.grepp.funfun.app.domain.admin.controller;

import com.grepp.funfun.app.domain.admin.dto.AdminReportViewDTO;
import com.grepp.funfun.app.domain.admin.dto.payload.AdminReportProcessRequest;
import com.grepp.funfun.app.domain.admin.service.AdminReportService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportApiController {

    private final AdminReportService adminReportService;

    @GetMapping
    @Operation(summary = "관리자 신고 목록 조회", description = "status param으로 전체 / 처리완료 / 미처리 필터 가능 (all, resolved, unresolved)")
    public ResponseEntity<ApiResponse<List<AdminReportViewDTO>>> getAllReports(
            @RequestParam(defaultValue = "all") String status) {
        List<AdminReportViewDTO> reports = adminReportService.getAllReports(status);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "신고 처리", description = "신고에 대한 유저 제재 or 답변 등록 처리")
    public ResponseEntity<ApiResponse<String>> processReport(
            @PathVariable Long id,
            @RequestBody AdminReportProcessRequest request
            ){
            adminReportService.processReport(id, request);
            return ResponseEntity.ok(ApiResponse.success("신고가 정상적으로 처리되었습니다."));
    }
}
