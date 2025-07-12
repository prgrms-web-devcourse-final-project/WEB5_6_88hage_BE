package com.grepp.funfun.app.domain.chat.vo;

import lombok.Getter;

@Getter
public enum ChatRoomType {

    GROUP_CHAT("모임 채팅"),
    PERSONAL_CHAT("개인 채팅");

    private final String koreanName;

    ChatRoomType(String koreanName) {
        this.koreanName = koreanName;
    }

}
