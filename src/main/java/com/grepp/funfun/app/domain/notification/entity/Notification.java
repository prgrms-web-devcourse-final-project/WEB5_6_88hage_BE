package com.grepp.funfun.app.domain.notification.entity;

import com.grepp.funfun.app.domain.notification.vo.NotificationType;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String message;

    private String link;

    private Boolean isRead = false;

    public void markAsRead() {
        this.isRead = true;
    }

    @Enumerated(EnumType.STRING)
    private NotificationType type; // notice, schedule 두가지

    private LocalDateTime scheduledAt;

    private LocalDateTime sentAt;
}
