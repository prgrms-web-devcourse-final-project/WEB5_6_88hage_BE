package com.grepp.funfun.app.controller.api.bookmark;

import com.grepp.funfun.app.model.bookmark.dto.ContentBookMarkDTO;
import com.grepp.funfun.app.model.bookmark.service.ContentBookMarkService;
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
@RequestMapping(value = "/api/contentBookMarks", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentBookMarkApiController {

    private final ContentBookMarkService contentBookMarkService;

    public ContentBookMarkApiController(final ContentBookMarkService contentBookMarkService) {
        this.contentBookMarkService = contentBookMarkService;
    }

    @GetMapping
    public ResponseEntity<List<ContentBookMarkDTO>> getAllContentBookMarks() {
        return ResponseEntity.ok(contentBookMarkService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentBookMarkDTO> getContentBookMark(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(contentBookMarkService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createContentBookMark(
            @RequestBody @Valid final ContentBookMarkDTO contentBookMarkDTO) {
        final Long createdId = contentBookMarkService.create(contentBookMarkDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateContentBookMark(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ContentBookMarkDTO contentBookMarkDTO) {
        contentBookMarkService.update(id, contentBookMarkDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteContentBookMark(@PathVariable(name = "id") final Long id) {
        contentBookMarkService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
