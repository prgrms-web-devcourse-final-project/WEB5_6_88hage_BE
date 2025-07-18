package com.grepp.funfun.app.domain.notification.controller;

import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.service.NotificationService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록 조회", description = "모든 알림 또는 특정 사용자의 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(
            @RequestParam(required = false) String email) {

        List<NotificationDTO> result = (email != null)
                ? notificationService.findByEmail(email)
                : notificationService.findAll();

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/recent")
    @Operation(summary = "최근 알림 조회", description = "사용자의 최근 알림 10개를 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getRecentNotifications(
            @RequestParam String email) {
        List<NotificationDTO> result = notificationService.findRecentByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/unread")
    @Operation(summary = "안읽은 알림 조회", description = "사용자가 읽지 않은 (클릭하지 않은) 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotifications(@RequestParam String email) {
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

    @DeleteMapping("/{id}")
    @Operation(summary = "단건 알림 삭제", description = "알림을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    @Operation(summary = "모든 알림 삭제", description = "해당 사용자의 모든 알림을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAllNotifications(@RequestParam String email) {
        notificationService.deleteAllByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    @Operation(summary = "선택 알림 삭제", description = "선택한 알림들을 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteSelected(@RequestBody List<Long> ids) {
        notificationService.deleteSelected(ids);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 안읽은 알림 수 조회
    @Operation(summary = "안읽은 알림 수 조회", description = "해당 사용자의 읽지 않은 알림 수를 반환합니다.")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(@RequestParam String email) {
        int count = notificationService.countUnread(email);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 개별 알림 읽음 처리
    @Operation(summary = "개별 알림 읽음 처리", description = "알림을 읽음 처리합니다.")
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 전체 알림 읽음 처리
    @Operation(summary = "전체 알림 읽음 처리", description = "해당 사용자의 모든 알림을 읽음 처리합니다.")
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@RequestParam String email) {
        notificationService.markAllAsRead(email);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
