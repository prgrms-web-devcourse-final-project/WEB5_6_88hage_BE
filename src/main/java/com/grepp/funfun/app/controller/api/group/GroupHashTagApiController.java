package com.grepp.funfun.app.controller.api.group;

import com.grepp.funfun.app.model.group.dto.GroupHashTagDTO;
import com.grepp.funfun.app.model.group.service.GroupHashTagService;
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
@RequestMapping(value = "/api/groupHashTags", produces = MediaType.APPLICATION_JSON_VALUE)
public class GroupHashTagApiController {

    private final GroupHashTagService groupHashTagService;

    public GroupHashTagApiController(final GroupHashTagService groupHashTagService) {
        this.groupHashTagService = groupHashTagService;
    }

    @GetMapping
    public ResponseEntity<List<GroupHashTagDTO>> getAllGroupHashTags() {
        return ResponseEntity.ok(groupHashTagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupHashTagDTO> getGroupHashTag(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(groupHashTagService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createGroupHashTag(
            @RequestBody @Valid final GroupHashTagDTO groupHashTagDTO) {
        final Long createdId = groupHashTagService.create(groupHashTagDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateGroupHashTag(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final GroupHashTagDTO groupHashTagDTO) {
        groupHashTagService.update(id, groupHashTagDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteGroupHashTag(@PathVariable(name = "id") final Long id) {
        groupHashTagService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
