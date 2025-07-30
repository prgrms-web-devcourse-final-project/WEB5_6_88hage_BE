package com.grepp.funfun.app.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NotificationDTO {

    private final Long id;

    @Size(max = 255)
    private final String email;

    @Size(max = 255)
    private final String message;

    private final String link;

    @JsonProperty("isRead")
    private final Boolean isRead;

    private final String type; // NOTICE, SCHEDULE

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime scheduledAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime sentAt;

    @Schema(description = "일정 ID(해당 알림이 어떤 캘린더 일정과 연관되는지 확인용)")
    private final Long calendarId;

    public NotificationDTO(
            Long id,
            String email,
            String message,
            String link,
            Boolean isRead,
            String type,
            LocalDateTime scheduledAt,
            LocalDateTime sentAt,
            Long calendarId
    ) {
        this.id = id;
        this.email = email;
        this.message = message;
        this.link = link;
        this.isRead = isRead;
        this.type = type;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.calendarId = calendarId;
    }
}
