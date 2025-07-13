package com.grepp.funfun.app.domain.content.vo;


import lombok.Getter;

@Getter
public enum ContentClassification {

    THEATER("연극"),
    DANCE("무용(서양/한국무용)"),
    POP_DANCE("대중무용"),
    CLASSIC("서양음악(클래식)"),
    GUKAK("한국음악(국악)"),
    POP_MUSIC("대중음악"),
    MIX("복합"),
    MAGIC("서커스/마술"),
    MUSICAL("뮤지컬"),
    TOUR("관광지"),
    CULTURE("문화시설"),
    SPORTS("레포츠");

    private final String koreanName;

    ContentClassification(String koreanName) {
        this.koreanName = koreanName;
    }

}
