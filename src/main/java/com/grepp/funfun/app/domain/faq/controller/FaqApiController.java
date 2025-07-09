package com.grepp.funfun.app.domain.faq.controller;

import com.grepp.funfun.app.domain.faq.dto.payload.FaqCreateRequest;
import com.grepp.funfun.app.domain.faq.dto.payload.FaqUpdateRequest;
import com.grepp.funfun.app.domain.faq.dto.FaqDTO;
import com.grepp.funfun.app.domain.faq.service.FaqService;
import com.grepp.funfun.app.infra.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<FaqDTO>>> getAllFaqs(){
        return ResponseEntity.ok(ApiResponse.success(faqService.findAll()));
    }

    // FAQ 상세조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FaqDTO>> getFaqById(@PathVariable final Long id){
        return ResponseEntity.ok(ApiResponse.success(faqService.get(id)));
    }

    // FAQ 생성
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createFaq(@RequestBody @Valid final FaqCreateRequest request){
        Long id = faqService.createFromRequest(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateFaq(@PathVariable Long id, @RequestBody @Valid final FaqUpdateRequest request){
        faqService.updateFromRequest(id, request);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    // FAQ 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFaq(@PathVariable final Long id){
        faqService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
