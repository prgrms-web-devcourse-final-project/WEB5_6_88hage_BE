package com.grepp.funfun.app.model.calendar.code;


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
