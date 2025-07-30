package com.grepp.funfun.app.domain.notification.controller;

import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.service.NotificationService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "내 알림 목록 조회", description = "로그인한 사용자의 알림을 모두 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getMyNotifications(Authentication authentication) {
        String email = authentication.getName();
        List<NotificationDTO> result = notificationService.findByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/recent")
    @Operation(summary = "최근 알림 조회", description = "로그인한 사용자의 최근 알림 10개를 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getRecentNotifications(Authentication authentication) {
        String email = authentication.getName();
        List<NotificationDTO> result = notificationService.findRecentByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/unread")
    @Operation(summary = "안읽은 알림 조회", description = "로그인한 사용자의 읽지 않은 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications(Authentication authentication) {
        String email = authentication.getName();
        List<NotificationDTO> result = notificationService.findUnreadByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    @Operation(summary = "알림 생성", description = "공지사항 등 알림을 생성합니다.")
    public ResponseEntity<ApiResponse<Long>> createNotification(@RequestBody @Valid NotificationDTO dto) {
        Long id = notificationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "알림 수정", description = "알림 내용을 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateNotification(
            @PathVariable Long id,
            @RequestBody @Valid NotificationDTO dto) {
        notificationService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 안읽은 알림 수 조회
    @GetMapping("/unread-count")
    @Operation(summary = "안읽은 알림 수 조회", description = "로그인한 사용자의 읽지 않은 알림 수를 조회합니다.")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(Authentication authentication) {
        String email = authentication.getName();
        int count = notificationService.countUnread(email);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 개별 알림 읽음 처리
    @PatchMapping("/{id}/read")
    @Operation(summary = "개별 알림 읽음 처리", description = "클릭하여 조회한 알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "전체 알림 읽음 처리", description = "로그인한 사용자의 모든 알림을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(Authentication authentication) {
        String email = authentication.getName();
        notificationService.markAllAsRead(email);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/read")
    @Operation(summary = "읽은 알림 조회", description = "로그인한 사용자의 읽은 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getReadNotifications(Authentication authentication) {
        String email = authentication.getName();
        List<NotificationDTO> result = notificationService.findReadByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 체크박스 선택 알림 읽음 처리
    @PatchMapping("/read-selected")
    @Operation(summary = "선택 알림 읽음 처리", description = "체크박스로 선택한 알림들을 읽음 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> markSelectedAsRead(@RequestBody List<Long> ids) {
        notificationService.markSelectedAsRead(ids);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

}
