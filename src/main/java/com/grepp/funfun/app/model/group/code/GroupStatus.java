package com.grepp.funfun.app.model.group.code;


public enum GroupStatus {

    RECRUITING("모집 중"),
    FULL("모집 마감"),
    COMPLETED("모임 완료"),
    CANCELED("모임 취소");

    private final String koreanName;

    GroupStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
