package com.grepp.funfun.app.domain.faq.controller;

import com.grepp.funfun.app.domain.faq.dto.payload.FaqCreateRequest;
import com.grepp.funfun.app.domain.faq.dto.payload.FaqUpdateRequest;
import com.grepp.funfun.app.domain.faq.dto.FaqDTO;
import com.grepp.funfun.app.domain.faq.service.FaqService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/faqs", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class FaqApiController {

    private final FaqService faqService;

    @GetMapping
    @Operation(summary = "전체 faq 조회", description = "모든 faq 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FaqDTO>>> getAllFaqs(){
        return ResponseEntity.ok(ApiResponse.success(faqService.findAll()));
    }

    // FAQ 상세조회
    @GetMapping("/{id}")
    @Operation(summary = "faq 상세 조회", description = "특정 faq 를 상세조회합니다.")
    public ResponseEntity<ApiResponse<FaqDTO>> getFaqById(@PathVariable final Long id){
        return ResponseEntity.ok(ApiResponse.success(faqService.get(id)));
    }

    // FAQ 생성
    @PostMapping
    @Operation(summary = "faq 신규 생성", description = "신규 faq 를 생성합니다.")
    public ResponseEntity<ApiResponse<Long>> createFaq(@RequestBody @Valid final FaqCreateRequest request){
        Long id = faqService.createFromRequest(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "faq 수정", description = "가존 생성된 faq 를 수정합니다.")
    public ResponseEntity<ApiResponse<Long>> updateFaq(@PathVariable Long id, @RequestBody @Valid final FaqUpdateRequest request){
        faqService.updateFromRequest(id, request);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    // FAQ 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "faq 삭제", description = "faq 를 삭제합니다. 되돌릴 수 없습니다.")
    public ResponseEntity<ApiResponse<Void>> deleteFaq(@PathVariable final Long id){
        faqService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
