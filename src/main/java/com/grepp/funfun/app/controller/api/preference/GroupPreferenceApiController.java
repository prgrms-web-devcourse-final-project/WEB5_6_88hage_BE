package com.grepp.funfun.app.controller.api.preference;

import com.grepp.funfun.app.model.preference.dto.GroupPreferenceDTO;
import com.grepp.funfun.app.model.preference.service.GroupPreferenceService;
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
@RequestMapping(value = "/api/groupPreferences", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupPreferenceApiController {

    private final GroupPreferenceService groupPreferenceService;

    public GroupPreferenceApiController(final GroupPreferenceService groupPreferenceService) {
        this.groupPreferenceService = groupPreferenceService;
    }

    @GetMapping
    public ResponseEntity<List<GroupPreferenceDTO>> getAllGroupPreferences() {
        return ResponseEntity.ok(groupPreferenceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPreferenceDTO> getGroupPreference(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(groupPreferenceService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createGroupPreference(
            @RequestBody @Valid final GroupPreferenceDTO groupPreferenceDTO) {
        final Long createdId = groupPreferenceService.create(groupPreferenceDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGroupPreference(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GroupPreferenceDTO groupPreferenceDTO) {
        groupPreferenceService.update(id, groupPreferenceDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteGroupPreference(@PathVariable(name = "id") final Long id) {
        groupPreferenceService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
