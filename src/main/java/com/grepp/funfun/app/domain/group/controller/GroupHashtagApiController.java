package com.grepp.funfun.app.domain.group.controller;

import com.grepp.funfun.app.domain.group.dto.GroupHashtagDTO;
import com.grepp.funfun.app.domain.group.service.GroupHashtagService;
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
@RequestMapping(value = "/api/groupHashtags", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupHashtagApiController {

    private final GroupHashtagService groupHashtagService;

    public GroupHashtagApiController(final GroupHashtagService groupHashtagService) {
        this.groupHashtagService = groupHashtagService;
    }

    @GetMapping
    public ResponseEntity<List<GroupHashtagDTO>> getAllGroupHashtags() {
        return ResponseEntity.ok(groupHashtagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupHashtagDTO> getGroupHashtag(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(groupHashtagService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createGroupHashtag(
            @RequestBody @Valid final GroupHashtagDTO groupHashtagDTO) {
        final Long createdId = groupHashtagService.create(groupHashtagDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGroupHashtag(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GroupHashtagDTO groupHashtagDTO) {
        groupHashtagService.update(id, groupHashtagDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteGroupHashtag(@PathVariable(name = "id") final Long id) {
        groupHashtagService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
