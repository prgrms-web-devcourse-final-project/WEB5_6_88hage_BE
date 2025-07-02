package com.grepp.funfun.app.model.participant.code;


public enum ParticipantStatus {

    PENDING("대기"),
    APPROVED("수락"),
    REJECTED("거절");

    private final String koreanName;

    ParticipantStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
