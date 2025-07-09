package com.grepp.funfun.app.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotificationDTO {

    private Long id;

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String message;

    @Size(max = 255)
    private String link;

    @JsonProperty("isRead")
    private Boolean isRead;

}
