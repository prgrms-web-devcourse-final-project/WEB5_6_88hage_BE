package com.grepp.funfun.app.domain.notification.mapper;

import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.entity.Notification;
import com.grepp.funfun.app.domain.notification.vo.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class NotificationDTOMapper {

    public NotificationDTO toDTO(Notification entity) {
        return NotificationDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .message(entity.getMessage())
                .link(entity.getLink())
                .isRead(entity.getIsRead())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .scheduledAt(entity.getScheduledAt())
                .sentAt(entity.getSentAt())
                .build();
    }

    public Notification toEntity(NotificationDTO dto) {
        return Notification.builder()
                .email(dto.getEmail())
                .message(dto.getMessage())
                .link(dto.getLink() != null ? dto.getLink() : "/calendar")
                .isRead(dto.getIsRead() != null ? dto.getIsRead() : false)
                .type(dto.getType() != null ? NotificationType.valueOf(dto.getType().toUpperCase()) : NotificationType.NOTICE)
                .scheduledAt(dto.getScheduledAt())
                .sentAt(dto.getSentAt())
                .build();
    }

}
