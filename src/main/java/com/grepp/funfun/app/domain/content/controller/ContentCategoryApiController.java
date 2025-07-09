package com.grepp.funfun.app.domain.content.controller;

import com.grepp.funfun.app.domain.content.dto.ContentCategoryDTO;
import com.grepp.funfun.app.domain.content.service.ContentCategoryService;
import com.grepp.funfun.app.delete.util.ReferencedException;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
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
@RequestMapping(value = "/api/contentCategories", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentCategoryApiController {

    private final ContentCategoryService contentCategoryService;

    public ContentCategoryApiController(final ContentCategoryService contentCategoryService) {
        this.contentCategoryService = contentCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<ContentCategoryDTO>> getAllContentCategories() {
        return ResponseEntity.ok(contentCategoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentCategoryDTO> getContentCategory(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(contentCategoryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createContentCategory(
            @RequestBody @Valid final ContentCategoryDTO contentCategoryDTO) {
        final Long createdId = contentCategoryService.create(contentCategoryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateContentCategory(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ContentCategoryDTO contentCategoryDTO) {
        contentCategoryService.update(id, contentCategoryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteContentCategory(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = contentCategoryService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        contentCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
