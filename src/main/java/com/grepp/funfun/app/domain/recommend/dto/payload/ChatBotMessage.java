package com.grepp.funfun.app.domain.recommend.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatBotMessage {

    private String user;
    private String ai;

}
