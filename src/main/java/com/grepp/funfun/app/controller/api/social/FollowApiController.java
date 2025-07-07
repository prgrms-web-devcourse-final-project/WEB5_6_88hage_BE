package com.grepp.funfun.app.controller.api.social;

import com.grepp.funfun.app.controller.api.social.payload.FollowsResponse;
import com.grepp.funfun.app.model.social.service.FollowService;
import com.grepp.funfun.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/followers")
    @Operation(summary = "팔로워 조회", description = "팔로워 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FollowsResponse>>> getFollowers(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.getFollowers(email)));
    }

    @GetMapping("/followings")
    @Operation(summary = "팔로잉 조회", description = "팔로잉 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FollowsResponse>>> getFollowings(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.getFollowings(email)));
    }

    @GetMapping("/count/followers")
    @Operation(summary = "팔로워 수 조회", description = "팔로워 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countFollowers(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.countFollowers(email)));
    }

    @GetMapping("/count/followings")
    @Operation(summary = "팔로잉 수 조회", description = "팔로잉 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Long>> countFollowings(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.countFollowings(email)));
    }

    @GetMapping("/status")
    @Operation(summary = "팔로우 여부 확인", description = "특정 사용자의 팔로우 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(@RequestParam String target, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.isFollowing(email, target)));
    }

}
