package com.grepp.funfun.app.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GroupJoinNotificationDTO extends NotificationDTO {

    private final String applicantEmail;

    @Builder(builderMethodName = "groupJoinBuilder")
    public GroupJoinNotificationDTO(
            Long id,
            String email,
            String message,
            String link,
            Boolean isRead,
            String type,
            LocalDateTime scheduledAt,
            LocalDateTime sentAt,
            Long calendarId,
            String applicantEmail
    ) {
        super(id, email, message, link, isRead, type, scheduledAt, sentAt, calendarId);
        this.applicantEmail = applicantEmail;
    }
}
