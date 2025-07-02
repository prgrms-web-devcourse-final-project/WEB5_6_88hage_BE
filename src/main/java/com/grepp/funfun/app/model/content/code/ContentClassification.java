package com.grepp.funfun.app.model.content.code;


public enum ContentClassification {

    EDUCATION("교육/체험"),
    CLASSIC("클래식"),
    PLAY("연극"),
    MUSICAL("뮤지컬"),
    DANCE("무용"),
    ART("전시/미술"),
    GUKAK("국악"),
    MOVIE("영화"),
    FESTIVAL("축제"),
    CONCERT("콘서트"),
    SOLO("독주/독창회");

    private final String koreanName;

    ContentClassification(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

}
