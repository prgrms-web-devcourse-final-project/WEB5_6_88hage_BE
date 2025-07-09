package com.grepp.funfun.app.domain.calendar.vo;


public enum ActivityType {

    CONTENT("컨텐츠"),
    GROUP("모임");

    private final String koreanName;

    ActivityType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
