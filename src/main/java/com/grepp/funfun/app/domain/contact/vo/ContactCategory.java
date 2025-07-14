package com.grepp.funfun.app.domain.contact.vo;

public enum ContactCategory {
    GENERAL("일반"),
    REPORT("특정 사용자 신고");

    private final String koreanName;

    ContactCategory(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
