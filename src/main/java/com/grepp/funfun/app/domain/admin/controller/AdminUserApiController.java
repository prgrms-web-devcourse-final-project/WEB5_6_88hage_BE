package com.grepp.funfun.app.domain.admin.controller;

import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.user.dto.UserDTO;
import com.grepp.funfun.app.domain.admin.service.AdminUserService;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserApiController {

    private final AdminUserService adminUserService;

    private void checkAdminRole(UserDTO user) {
        if(user.getRole() != Role.ROLE_ADMIN) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
    }

    // 유저 전체 조회
    @GetMapping
    @Operation(summary= "유저 전체 조회", description = "모든 유저를 조회힙나디.")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        List<UserDTO> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{email}")
    @Operation(summary= "유저 상세 조회", description = "특정 유저를 상세 조회합니다.")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String email, Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        UserDTO user = adminUserService.getUser(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // 유저 제재 - 7일, 30일, 영구정지
    // @param duration: 7, 30, -1
    @PatchMapping("/{email}/suspend")
    @Operation(summary= "유저 제재 적용", description= "특정 유저에게 7, 30일 일시정지 또는 영구정지(-1)조치를 취합니다.")
    public ResponseEntity<ApiResponse<String>> suspendUser(
            @PathVariable String email,
            @RequestParam int duration,
            @RequestParam(required = false) String reason,
            Authentication authentication){
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        adminUserService.suspendUser(email, duration, reason);
        return ResponseEntity.ok(ApiResponse.success("회원 정지 완료"));
    }

    // 닉네임 유저 검색
    @GetMapping("/nickname/{nickname}")
    @Operation(summary= "닉네임으로 유저 검색", description= "닉네임으로 특정 유저를 검색합니다.")
    public ResponseEntity<ApiResponse<UserDTO>> getIUserByNickname(@PathVariable String nickname,
                                                                   Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        UserDTO user = adminUserService.getUserByNickname(nickname);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // 부적절한 닉네임 임의 숫자값 변경
    @PostMapping("/{email}/randomize-nickname")
    @Operation(summary= "부적절한 닉네임 임의 숫자값 부여", description = "유저의 닉네임을 임의의 8자리 숫자값으로 수정합니다.")
    public ResponseEntity<ApiResponse<String>> randomizeNickname(
            @PathVariable String email,
            Authentication authentication) {

        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        String newNickname = adminUserService.randomizeNickname(email);
        return ResponseEntity.ok(ApiResponse.success(newNickname));
    }

}
