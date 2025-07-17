package com.grepp.funfun.app.domain.admin.service;

import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.domain.user.dto.UserDTO;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserService userService;
    private final UserRepository userRepository;

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

    // 매일 새벽 6시에 일시정지 만료 유저 자동 복구
    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void autoUnsuspendedUser() {
        List<User> suspendedUsers = userRepository.findAllByStatus(UserStatus.SUSPENDED);
        LocalDate today = LocalDate.now();

        int updatedCount = 0;

        // duedate 가 오늘 or 이후인 경우에 정지 해제
        for(User user : suspendedUsers) {
            if(user.getDueDate() != null && !user.getDueDate().isAfter(today)) {
                user.setStatus(UserStatus.ACTIVE);
                user.setDueDate(null);
                user.setSuspendDuration(null);
                user.setDueReason(null);
                updatedCount++;
            }
        }

        if(updatedCount > 0) {
            userRepository.saveAll(suspendedUsers);
            log.info("자동 정지 해제 : {}명", updatedCount);
        } else {
            log.info("자동 정지 해제 대상 없음");
        }
    }

    @Transactional(readOnly = false)
    public UserDTO get(String email) {
        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        // 상태 복구 조건 검사
        if ((user.getStatus() == UserStatus.SUSPENDED &&
                user.getDueDate() != null &&
                !user.getDueDate().isAfter(LocalDate.now()))|| (UserStatus.NONACTIVE.equals(user.getStatus()))) {
            user.setStatus(UserStatus.ACTIVE);
            user.setDueDate(null);
            user.setSuspendDuration(null);
            user.setDueReason(null);
            userRepository.save(user);
        }

        return UserDTO.from(user);
    }

    @Transactional
    public String randomizeNickname(String email) {
        User user = userRepository.findOptionalByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        String newNickname;
        do {
            newNickname = String.valueOf((int)(Math.random() * 90000000) + 10000000);
        } while (userRepository.existsByNickname(newNickname));

        user.setNickname(newNickname);

        user.setStatus(UserStatus.NONACTIVE);

        userRepository.save(user);

        return newNickname;
    }

    // 닉네임으로 유저 찾기
    public UserDTO getUserByNickname(String nickname) {
        return userService.getUserByNickname(nickname);
    }
}