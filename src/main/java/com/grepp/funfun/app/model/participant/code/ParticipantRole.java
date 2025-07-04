package com.grepp.funfun.app.model.participant.code;

import javax.annotation.processing.Generated;
import lombok.Getter;

@Getter
public enum ParticipantRole {

    LEADER("주최자"),
    MEMBER("참여자");

    private final String koreanName;

    ParticipantRole(String koreanName) {
        this.koreanName = koreanName;
    }

}
