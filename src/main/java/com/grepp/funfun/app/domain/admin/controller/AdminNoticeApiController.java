package com.grepp.funfun.app.domain.admin.controller;

import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.service.NotificationService;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notices")
public class AdminNoticeApiController {

    private final NotificationService notificationService;
    private final UserService userService;

    @PostMapping("/notice")
    @Operation(summary = "전체 사용자에게 공지사항 발송", description = "관리자가 공지사항 알림을 모든 사용자에게 일괄 전송합니다.")
    public ResponseEntity<ApiResponse<Void>> broadcastNotice(@RequestBody @Valid NotificationDTO dto) {

        List<String> allEmails = userService.getAllUserEmails();

        for (String email : allEmails) {
            NotificationDTO individualDTO = NotificationDTO.builder()
                    .email(email)
                    .message(dto.getMessage())
                    .link(dto.getLink())
                    .isRead(false)
                    .type("NOTICE")
                    .scheduledAt(dto.getScheduledAt() != null ? dto.getScheduledAt() : LocalDateTime.now())
                    .sentAt(LocalDateTime.now())
                    .build();

            notificationService.create(individualDTO);
        }

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}