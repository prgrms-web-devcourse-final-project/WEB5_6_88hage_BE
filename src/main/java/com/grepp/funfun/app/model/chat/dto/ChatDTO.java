package com.grepp.funfun.app.model.chat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatDTO {

    private Long id;

    @Size(max = 255)
    private String senderNickname;

    @Size(max = 255)
    private String senderEmail;

    @Size(max = 255)
    private String message;

    private LocalDateTime sendDate;

    @NotNull
    private Long room;

}
