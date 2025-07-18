package com.grepp.funfun.app.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
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
}
