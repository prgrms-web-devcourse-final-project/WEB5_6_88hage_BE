package com.grepp.funfun.app.domain.admin.controller;

import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.user.dto.UserDTO;
import com.grepp.funfun.app.domain.admin.service.AdminUserService;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;

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
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        List<UserDTO> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable String email, Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        UserDTO user = adminUserService.getUser(email);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // 유저 제재 - 7일, 30일, 영구정지
    // @param duration: 7, 30, -1
    @PatchMapping("/{email}/suspend")
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
    public ResponseEntity<ApiResponse<UserDTO>> getIUserByNickname(@PathVariable String nickname,
                                                                   Authentication authentication) {
        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        UserDTO user = adminUserService.getUserByNickname(nickname);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // 부적절한 닉네임 임의 숫자값 변경
    @PostMapping("/{email}/randomize-nickname")
    public ResponseEntity<ApiResponse<String>> randomizeNickname(
            @PathVariable String email,
            Authentication authentication) {

        UserDTO admin = adminUserService.getAuthenticatedUser(authentication);
        checkAdminRole(admin);

        String newNickname = adminUserService.randomizeNickname(email);
        return ResponseEntity.ok(ApiResponse.success(newNickname));
    }

}
