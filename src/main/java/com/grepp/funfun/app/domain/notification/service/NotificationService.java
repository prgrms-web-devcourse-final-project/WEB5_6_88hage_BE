package com.grepp.funfun.app.domain.notification.service;

import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.entity.Notification;
import com.grepp.funfun.app.domain.notification.mapper.NotificationDTOMapper;
import com.grepp.funfun.app.domain.notification.repository.NotificationRepository;
import com.grepp.funfun.app.domain.notification.sse.EmitterRepository;
import com.grepp.funfun.app.domain.notification.vo.NotificationType;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final NotificationDTOMapper notificationDTOMapper;

    // 모든 알림 조회 - 어떤 사용자임과 관계없이
    public List<NotificationDTO> findAll() {
        return notificationRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(notificationDTOMapper::toDTO)
                .toList();
    }

    // 최근 10건 알림 조회
    public List<NotificationDTO> findRecentByEmail(String email) {
        return notificationRepository.findTop10ByEmailOrderByIdDesc(email).stream()
                .map(notificationDTOMapper::toDTO)
                .toList();
    }

    // 이메일 기준 알림 조회
    public List<NotificationDTO> findByEmail(String email) {
        return notificationRepository.findAllByEmailOrderByIdDesc(email).stream()
                .map(notificationDTOMapper::toDTO)
                .toList();
    }

    // 이메일 기준 안읽은 알림 조회
    public List<NotificationDTO> findUnreadByEmail(String email) {
        return notificationRepository.findAllByEmailAndIsReadFalseOrderByIdDesc(email)
                .stream().map(notificationDTOMapper::toDTO).toList();
    }

    public List<NotificationDTO> findReadByEmail(String email) {
        return notificationRepository.findAllByEmailAndIsReadTrueOrderByIdDesc(email)
                .stream().map(notificationDTOMapper::toDTO).toList();
    }

    // 체크박스 선택적 읽음 처리
    public void markSelectedAsRead(List<Long> ids) {
        List<Notification> selected = notificationRepository.findAllById(ids);

        for (Notification notification : selected) {
            if (!Boolean.TRUE.equals(notification.getIsRead())) {
                notification.markAsRead();
            }
        }

        notificationRepository.saveAll(selected);
    }

    // 알림 생성
    public Long create(final NotificationDTO dto) {
        Notification notification = notificationDTOMapper.toEntity(dto);

        Notification saved = notificationRepository.save(notification);
        notifyViaSse(saved);
        return saved.getId();
    }

    // 알림 수정
    public void update(final Long id, final NotificationDTO dto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        Notification updated = Notification.builder()
                .id(notification.getId())
                .email(dto.getEmail() != null ? dto.getEmail() : notification.getEmail())
                .message(dto.getMessage() != null ? dto.getMessage() : notification.getMessage())
                .link(dto.getLink() != null ? dto.getLink() : notification.getLink())
                .isRead(dto.getIsRead() != null ? dto.getIsRead() : notification.getIsRead())
                .type(dto.getType() != null ? parseType(dto.getType()) : notification.getType())
                .scheduledAt(dto.getScheduledAt() != null ? dto.getScheduledAt() : notification.getScheduledAt())
                .sentAt(dto.getSentAt() != null ? dto.getSentAt() : notification.getSentAt())
                .calendarId(dto.getCalendarId() != null ? dto.getCalendarId() : notification.getCalendarId())
                .build();

        notificationRepository.save(updated);
    }

    // String → Enum 변환 with null-safe
    private NotificationType parseType(String type) {
        if (type == null) return NotificationType.NOTICE;
        try {
            return NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "유효하지 않은 알림 유형입니다: " + type);
        }
    }

    // 안읽은 알림 개수 조회
    public int countUnread(String email) {
        return notificationRepository.countByEmailAndIsReadFalse(email);
    }

    // 알림 단건 읽음 처리
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }

    // 전체 읽음 처리
    public void markAllAsRead(String email) {
        List<Notification> unreadList = notificationRepository.findAllByEmailAndIsReadFalseOrderByIdDesc(email);
        for (Notification notification : unreadList) {
            notification.markAsRead();
        }
        notificationRepository.saveAll(unreadList);
    }

    public void notifyViaSse(Notification notification) {
        SseEmitter emitter = emitterRepository.get(notification.getEmail());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notificationDTOMapper.toDTO(notification)));
            } catch (IOException e) {
                emitterRepository.delete(notification.getEmail());
            }
        }
    }

    public boolean existsScheduleNotification(String email, LocalDateTime start, LocalDateTime end) {
        return notificationRepository.existsByEmailAndTypeAndScheduledAtBetween(
                email, NotificationType.SCHEDULE, start, end);
    }

    // calendarId 기반 중복 알림 확인
    public boolean existsScheduleNotification(Long calendarId) {
        return notificationRepository.existsByCalendarId(calendarId);
    }

    // 일정 삭제 시 알림 함께 삭제
    public void deleteByCalendarId(Long calendarId) {
        notificationRepository.deleteByCalendarId(calendarId);
    }
}
