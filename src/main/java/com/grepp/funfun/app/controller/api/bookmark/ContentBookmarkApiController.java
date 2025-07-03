package com.grepp.funfun.app.controller.api.bookmark;

import com.grepp.funfun.app.model.bookmark.dto.ContentBookmarkDTO;
import com.grepp.funfun.app.model.bookmark.service.ContentBookmarkService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/contentBookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentBookmarkApiController {

    private final ContentBookmarkService contentBookmarkService;

    public ContentBookmarkApiController(final ContentBookmarkService contentBookmarkService) {
        this.contentBookmarkService = contentBookmarkService;
    }

    @GetMapping
    public ResponseEntity<List<ContentBookmarkDTO>> getAllContentBookmarks() {
        return ResponseEntity.ok(contentBookmarkService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentBookmarkDTO> getContentBookmark(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(contentBookmarkService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createContentBookmark(
            @RequestBody @Valid final ContentBookmarkDTO contentBookmarkDTO) {
        final Long createdId = contentBookmarkService.create(contentBookmarkDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateContentBookmark(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ContentBookmarkDTO contentBookmarkDTO) {
        contentBookmarkService.update(id, contentBookmarkDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteContentBookmark(@PathVariable(name = "id") final Long id) {
        contentBookmarkService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
