package com.grepp.funfun.app.domain.group.vo;

import lombok.Getter;

@Getter
public enum GroupClassification {

    ART("예술"),
    TRAVEL("여행"),
    FOOD("음식"),
    GAME("게임"),
    CULTURE("문화"),
    SPORT("운동"),
    STUDY("자기 개발"),
    MOVIE("영화");

    private final String koreanName;

    GroupClassification(String koreanName) {
        this.koreanName = koreanName;
    }

}
