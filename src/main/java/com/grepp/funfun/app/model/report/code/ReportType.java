package com.grepp.funfun.app.model.report.code;


public enum ReportType {

    CHAT("채팅"),
    POST("게시글"),
    MESSAGE("쪽지");

    private final String koreanName;

    ReportType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
