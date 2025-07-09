package com.grepp.funfun.app.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatRoomDTO {

    private Long id;

    @NotNull
    private Long group;

}
