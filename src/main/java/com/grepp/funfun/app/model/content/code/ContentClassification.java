package com.grepp.funfun.app.model.content.code;


import com.fasterxml.jackson.annotation.JsonValue;

public enum ContentClassification {

    THEATER("연극"),
    DANCE("무용(서양/한국무용)"),
    POP_DANCE("대중무용"),
    CLASSIC("서양음악(클래식)"),
    GUKAK("한국음악(국악)"),
    POP_MUSIC("대중음악"),
    MIX("복합"),
    MAGIC("서커스/마술"),
    MUSICAL("뮤지컬");

    private final String koreanName;

    ContentClassification(String koreanName) {
        this.koreanName = koreanName;
    }

    @JsonValue
    public String getKoreanName() {
        return koreanName;
    }

}
