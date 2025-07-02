package com.grepp.funfun.app.model.user.code;


public enum UserStatus {

    ACTIVE("활성"),
    SUSPENDED("일시 정지"),
    BANNED("영구 정지");

    private final String koreanName;

    UserStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
