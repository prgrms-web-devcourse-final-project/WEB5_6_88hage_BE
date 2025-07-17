package com.grepp.funfun.app.domain.user.vo;


public enum UserStatus {

    ACTIVE("활성"),
    NONACTIVE("비활성"),
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
