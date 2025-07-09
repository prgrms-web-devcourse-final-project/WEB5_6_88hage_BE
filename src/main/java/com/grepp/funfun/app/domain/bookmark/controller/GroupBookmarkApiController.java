package com.grepp.funfun.app.domain.bookmark.controller;

import com.grepp.funfun.app.domain.bookmark.dto.GroupBookmarkDTO;
import com.grepp.funfun.app.domain.bookmark.service.GroupBookmarkService;
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
@RequestMapping(value = "/api/groupBookmarks", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupBookmarkApiController {

    private final GroupBookmarkService groupBookmarkService;

    public GroupBookmarkApiController(final GroupBookmarkService groupBookmarkService) {
        this.groupBookmarkService = groupBookmarkService;
    }

    @GetMapping
    public ResponseEntity<List<GroupBookmarkDTO>> getAllGroupBookmarks() {
        return ResponseEntity.ok(groupBookmarkService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupBookmarkDTO> getGroupBookmark(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(groupBookmarkService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createGroupBookmark(
            @RequestBody @Valid final GroupBookmarkDTO groupBookmarkDTO) {
        final Long createdId = groupBookmarkService.create(groupBookmarkDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGroupBookmark(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GroupBookmarkDTO groupBookmarkDTO) {
        groupBookmarkService.update(id, groupBookmarkDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteGroupBookmark(@PathVariable(name = "id") final Long id) {
        groupBookmarkService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
