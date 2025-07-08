package com.grepp.funfun.app.model.admin.service;

import com.grepp.funfun.app.model.user.code.UserStatus;
import com.grepp.funfun.app.model.user.dto.UserDTO;
import com.grepp.funfun.app.model.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserService userService;

    // admin 유저 조회
    public UserDTO getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.get(email);
    }

    // 모든 유저 조회
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }

    // 한명 상세 조회
    public UserDTO getUser(String email) {
        return userService.get(email);
    }

    // 유저 제재 ( 7/30/영정)
    // @param email , @param days, @param reason
    public void suspendUser(String email, int days, String reason) {
        UserDTO user = userService.get(email);

        if(days == -1) {
            user.setStatus(UserStatus.BANNED);
            user.setDueDate(null);
        } else {
            user.setStatus(UserStatus.SUSPENDED);
            user.setDueDate(LocalDate.now().plusDays(days));
        }

        user.setSuspendDuration(days);
        user.setDueReason(reason);

        userService.update(email, user);
    }

    // 닉네임으로 유저 찾기
    public UserDTO getUserByNickname(String nickname) {
        return userService.getUserByNickname(nickname);
    }
}
