package com.grepp.funfun.app.domain.contact.vo;


public enum ContactStatus {

    PENDING("답변 대기"),
    COMPLETE("답변 완료");

    private final String koreanName;

    ContactStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
