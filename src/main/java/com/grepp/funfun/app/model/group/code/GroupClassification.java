package com.grepp.funfun.app.model.group.code;


public enum GroupClassification {

    GAME("게임"),
    FOOD("푸드/드링크"),
    TRAVEL("여행/나들이"),
    ART("문화/예술"),
    STUDY("지식/자기계발"),
    SPORT("운동/신체활동");

    private final String koreanName;

    GroupClassification(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
