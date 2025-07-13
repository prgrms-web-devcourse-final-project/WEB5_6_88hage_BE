package com.grepp.funfun.app.domain.report.controller;

import com.grepp.funfun.app.domain.report.dto.payload.ReportRequest;
import com.grepp.funfun.app.domain.report.service.ReportService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ReportApiController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "신고하기", description = "부적절한 모임 게시글이나 채팅을 신고합니다.")
    public ResponseEntity<ApiResponse<String>> createReport(@RequestBody ReportRequest reportRequest, Authentication authentication) {
        String email = authentication.getName();
        reportService.create(email, reportRequest);
        return ResponseEntity.ok(ApiResponse.success("정상적으로 신고되었습니다."));
    }

}
