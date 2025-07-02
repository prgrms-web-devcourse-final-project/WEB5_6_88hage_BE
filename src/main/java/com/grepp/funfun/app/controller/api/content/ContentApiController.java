package com.grepp.funfun.app.controller.api.content;

import com.grepp.funfun.app.model.content.dto.ContentDTO;
import com.grepp.funfun.app.model.content.service.ContentService;
import com.grepp.funfun.util.ReferencedException;
import com.grepp.funfun.util.ReferencedWarning;
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
@RequestMapping(value = "/api/contents", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentApiController {

    private final ContentService contentService;

    public ContentApiController(final ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping
    public ResponseEntity<List<ContentDTO>> getAllContents() {
        return ResponseEntity.ok(contentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentDTO> getContent(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(contentService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createContent(@RequestBody @Valid final ContentDTO contentDTO) {
        final Long createdId = contentService.create(contentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateContent(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ContentDTO contentDTO) {
        contentService.update(id, contentDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteContent(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = contentService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        contentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
