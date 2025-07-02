package com.grepp.funfun.app.controller.api.preference;

import com.grepp.funfun.app.model.preference.dto.ContentPreferenceDTO;
import com.grepp.funfun.app.model.preference.service.ContentPreferenceService;
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
@RequestMapping(value = "/api/contentPreferences", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentPreferenceApiController {

    private final ContentPreferenceService contentPreferenceService;

    public ContentPreferenceApiController(final ContentPreferenceService contentPreferenceService) {
        this.contentPreferenceService = contentPreferenceService;
    }

    @GetMapping
    public ResponseEntity<List<ContentPreferenceDTO>> getAllContentPreferences() {
        return ResponseEntity.ok(contentPreferenceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentPreferenceDTO> getContentPreference(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(contentPreferenceService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createContentPreference(
            @RequestBody @Valid final ContentPreferenceDTO contentPreferenceDTO) {
        final Long createdId = contentPreferenceService.create(contentPreferenceDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateContentPreference(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ContentPreferenceDTO contentPreferenceDTO) {
        contentPreferenceService.update(id, contentPreferenceDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteContentPreference(@PathVariable(name = "id") final Long id) {
        contentPreferenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
