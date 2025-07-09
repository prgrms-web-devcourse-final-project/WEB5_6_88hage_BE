package com.grepp.funfun.app.domain.bookmark.controller;

import com.grepp.funfun.app.domain.bookmark.dto.ContentBookmarkDTO;
import com.grepp.funfun.app.domain.bookmark.service.ContentBookmarkService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/contentBookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ContentBookmarkApiController {

    private final ContentBookmarkService contentBookmarkService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContentBookmarkDTO>>> getAllContentBookmarks() {
        List<ContentBookmarkDTO> bookmarks = contentBookmarkService.findAll();
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<ContentBookmarkDTO>>> getContentBookmark(
            @RequestParam String email
//            Authentication authentication
    ) {
//        String currentUserEmail = authentication.getName();
        List<ContentBookmarkDTO> bookmarks = contentBookmarkService.getByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> addContentBookmark(
            @RequestBody @Valid final ContentBookmarkDTO contentBookmarkDTO,
            @RequestParam String email
//            Authentication authentication
    ) {
//        String currentUserEmail = authentication.getName();
        Long createdId = contentBookmarkService.addByEmail(contentBookmarkDTO, email);
        return new ResponseEntity<>(ApiResponse.success(createdId), HttpStatus.CREATED);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<Long>> updateContentBookmark(@PathVariable final Long id,
//            @RequestBody @Valid final ContentBookmarkDTO contentBookmarkDTO) {
//        contentBookmarkService.update(id, contentBookmarkDTO);
//        return ResponseEntity.ok(ApiResponse.success(id));
//    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContentBookmark(
            @PathVariable final Long id,
            @RequestParam String email
//            Authentication authentication
    ) {

//        String currentUserEmail = authentication.getName();
        contentBookmarkService.deleteByEmail(id, email);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
