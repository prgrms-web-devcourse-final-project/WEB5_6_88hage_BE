package com.grepp.funfun.app.model.social.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageDTO {

    private Long id;

    @Size(max = 255)
    private String content;

    @JsonProperty("isRead")
    private Boolean isRead;

    private LocalDateTime readAt;

    @NotNull
    @Size(max = 255)
    private String sender;

    @NotNull
    @Size(max = 255)
    private String receiver;

}
