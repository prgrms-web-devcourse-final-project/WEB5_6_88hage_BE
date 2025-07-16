package com.grepp.funfun.app.domain.social.controller;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import com.grepp.funfun.app.domain.social.service.FollowService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "팔로잉 조회", description = """
        팔로잉 목록을 조회합니다.
        기본 정렬은 닉네임 오름차순입니다.
        
        • page: 0 ~ N, 보고 싶은 페이지를 지정할 수 있습니다.
            - 기본값: 0
        
        • size: 기본 페이지당 항목 수
            - 기본값 : 10
        
        • sort: 정렬
        
            - 정렬 가능한 필드:
                        - `nickname` (닉네임)
                        - `createdAt` (팔로우한 시간)
        
            - 정렬 방식 예시:
                        - `?sort=createdAt,desc` (최신순)
                        - `?sort=createdAt,asc` (오래된순)
                        - `?sort=nickname,asc` (기본값, 닉네임 오름차순)
        """)
    public ResponseEntity<ApiResponse<Page<FollowsResponse>>> getFollowings(
        Authentication authentication,
        @Parameter(hidden = true)
        @ParameterObject
        @PageableDefault(sort = "nickname", direction = Sort.Direction.ASC) Pageable pageable) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.getFollowings(email, pageable)));
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

    @GetMapping("/status/following")
    @Operation(summary = "팔로잉 여부 확인", description = "특정 사용자에 대한 자신의 팔로잉 여부를 확인합니다.")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(@RequestParam String target, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.isFollowing(email, target)));
    }

    @GetMapping("/status/follower")
    @Operation(summary = "팔로워 여부 확인", description = "특정 사용자가 자신을 팔로우 했는지를 확인합니다.(팔로워 여부)")
    public ResponseEntity<ApiResponse<Boolean>> isFollower(@RequestParam String target, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(followService.isFollower(email, target)));
    }
}
