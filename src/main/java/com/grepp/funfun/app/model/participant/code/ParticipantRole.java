package com.grepp.funfun.app.model.participant.code;


public enum ParticipantRole {

    LEADER("주최자"),
    MEMBER("참여자");

    private final String koreanName;

    ParticipantRole(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
