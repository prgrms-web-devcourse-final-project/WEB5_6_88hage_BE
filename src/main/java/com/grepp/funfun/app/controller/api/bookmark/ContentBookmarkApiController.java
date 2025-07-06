package com.grepp.funfun.app.controller.api.bookmark;

import com.grepp.funfun.app.model.bookmark.dto.ContentBookmarkDTO;
import com.grepp.funfun.app.model.bookmark.service.ContentBookmarkService;
import com.grepp.funfun.infra.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/contentBookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentBookmarkApiController {

    private final ContentBookmarkService contentBookmarkService;

    public ContentBookmarkApiController(final ContentBookmarkService contentBookmarkService) {
        this.contentBookmarkService = contentBookmarkService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContentBookmarkDTO>>> getAllContentBookmarks() {
        List<ContentBookmarkDTO> bookmarks = contentBookmarkService.findAll();
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContentBookmarkDTO>> getContentBookmark(
            @PathVariable final Long id) {
        ContentBookmarkDTO bookmark = contentBookmarkService.get(id);
        return ResponseEntity.ok(ApiResponse.success(bookmark));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addContentBookmark(
            @RequestBody @Valid final ContentBookmarkDTO contentBookmarkDTO) {
        final Long createdId = contentBookmarkService.add(contentBookmarkDTO);
        return new ResponseEntity<>(ApiResponse.success(createdId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateContentBookmark(@PathVariable final Long id,
            @RequestBody @Valid final ContentBookmarkDTO contentBookmarkDTO) {
        contentBookmarkService.update(id, contentBookmarkDTO);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContentBookmark(
            @PathVariable final Long id) {
        contentBookmarkService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
