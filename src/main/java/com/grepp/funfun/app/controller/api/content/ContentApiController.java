package com.grepp.funfun.app.controller.api.content;

import com.grepp.funfun.app.controller.api.content.payload.ContentDetailResponse;
import com.grepp.funfun.app.controller.api.content.payload.ContentFilterRequest;
import com.grepp.funfun.app.model.content.dto.ContentDTO;
import com.grepp.funfun.app.model.content.service.ContentService;
import com.grepp.funfun.infra.response.ApiResponse;
import org.springframework.data.domain.Page;
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
public class ContentApiController {

    private final ContentService contentService;

    public ContentApiController(
            final ContentService contentService) {
        this.contentService = contentService;
    }

    // 컨텐츠 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ContentDTO>>> getAllContents(
            ContentFilterRequest request,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ContentDTO> contents = contentService.findByFilters(request, pageable);
        return ResponseEntity.ok(ApiResponse.success(contents));
    }

    // 컨텐츠 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentDetailResponse>> getContent(
            @PathVariable(name = "id") final Long id
    ) {
        ContentDTO content = contentService.get(id);
        List<ContentDTO> related = contentService.findRandomByCategory(id, 5);
        List<ContentDTO> nearby = contentService.findNearbyContents(id, 3.0, 5);

        ContentDetailResponse response = new ContentDetailResponse(content, related, nearby);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
