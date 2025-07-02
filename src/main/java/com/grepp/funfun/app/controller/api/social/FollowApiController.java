package com.grepp.funfun.app.controller.api.social;

import com.grepp.funfun.app.model.social.dto.FollowDTO;
import com.grepp.funfun.app.model.social.service.FollowService;
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
@RequestMapping(value = "/api/follows", produces = MediaType.APPLICATION_JSON_VALUE)
public class FollowApiController {

    private final FollowService followService;

    public FollowApiController(final FollowService followService) {
        this.followService = followService;
    }

    @GetMapping
    public ResponseEntity<List<FollowDTO>> getAllFollows() {
        return ResponseEntity.ok(followService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FollowDTO> getFollow(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(followService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createFollow(@RequestBody @Valid final FollowDTO followDTO) {
        final Long createdId = followService.create(followDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateFollow(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final FollowDTO followDTO) {
        followService.update(id, followDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteFollow(@PathVariable(name = "id") final Long id) {
        followService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
