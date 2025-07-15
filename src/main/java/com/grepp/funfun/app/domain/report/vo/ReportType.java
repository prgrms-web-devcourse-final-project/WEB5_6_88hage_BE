package com.grepp.funfun.app.domain.report.vo;


public enum ReportType {

    CHAT("채팅"),
    POST("게시글");

    private final String koreanName;

    ReportType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}
