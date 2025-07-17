package com.grepp.funfun.app.domain.content.controller;

import com.grepp.funfun.app.domain.content.dto.payload.ContentDetailResponse;
import com.grepp.funfun.app.domain.content.dto.payload.ContentFilterRequest;
import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.service.ContentService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "/api/contents", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ContentApiController {

    private final ContentService contentService;

    @GetMapping
    @Operation(summary = "컨텐츠 목록 조회")
    public ResponseEntity<ApiResponse<Page<ContentDTO>>> getAllContents(
            @Valid @ParameterObject ContentFilterRequest request,
            @ParameterObject Pageable pageable) {

        Page<ContentDTO> contents = contentService.findByFiltersWithSort(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(contents));

    }

    @GetMapping("/{id}")
    @Operation(summary = "컨텐츠 상세 조회")
    public ResponseEntity<ApiResponse<ContentDetailResponse>> getContent(
            @PathVariable Long id
    ) {
        ContentDTO content = contentService.getContents(id);
        List<ContentDTO> related = contentService.findRandomByCategory(id, 5, false);
        List<ContentDTO> nearby = contentService.findNearbyContents(id, 3.0, 5, false);

        ContentDetailResponse response = ContentDetailResponse.builder()
                .content(content)
                .related(related)
                .nearby(nearby)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
