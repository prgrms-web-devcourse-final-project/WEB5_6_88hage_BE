package com.grepp.funfun.app.model.group.code;

import lombok.Getter;

@Getter
public enum GroupClassification {

    GAME("게임"),
    FOOD("푸드/드링크"),
    TRAVEL("여행/나들이"),
    ART("예술"),
    CULTURE("문화"),
    STUDY("자기개발"),
    SPORT("운동/신체활동");

    private final String koreanName;

    GroupClassification(String koreanName) {
        this.koreanName = koreanName;
    }

}
