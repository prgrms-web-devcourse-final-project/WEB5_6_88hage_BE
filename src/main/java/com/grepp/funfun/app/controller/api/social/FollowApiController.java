package com.grepp.funfun.app.controller.api.social;

import com.grepp.funfun.app.model.social.dto.FollowDTO;
import com.grepp.funfun.app.model.social.service.FollowService;
import com.grepp.funfun.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequiredArgsConstructor
public class FollowApiController {

    private final FollowService followService;

    @PostMapping("/{targetEmail}")
    @Operation(summary = "팔로우", description = "원하는 사용자를 팔로우 합니다.")
    public ResponseEntity<ApiResponse<String>> follow(@PathVariable String targetEmail, Authentication authentication) {
        String email = authentication.getName();
        followService.follow(email, targetEmail);
        return ResponseEntity.ok(ApiResponse.success("팔로우에 성공했습니다."));
    }

    @DeleteMapping("/{targetEmail}")
    @Operation(summary = "언팔로우", description = "팔로우를 취소합니다.")
    public ResponseEntity<ApiResponse<String>> unfollow(@PathVariable String targetEmail, Authentication authentication) {
        String email = authentication.getName();
        followService.unfollow(email, targetEmail);
        return ResponseEntity.ok(ApiResponse.success("언팔로우에 성공했습니다."));
    }

    @GetMapping
    public ResponseEntity<List<FollowDTO>> getAllFollows() {
        return ResponseEntity.ok(followService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FollowDTO> getFollow(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(followService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateFollow(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final FollowDTO followDTO) {
        followService.update(id, followDTO);
        return ResponseEntity.ok(id);
    }

}
