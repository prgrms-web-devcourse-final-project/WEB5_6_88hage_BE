package com.grepp.funfun.app.model.group.code;

import lombok.Getter;

@Getter
public enum GroupStatus {

    RECRUITING("모집 중"),
    FULL("모집 마감"),
    COMPLETED("모임 완료"),
    CANCELED("모임 취소"),
    DELETE("모임 삭제");

    private final String koreanName;

    GroupStatus(String koreanName) {
        this.koreanName = koreanName;
    }

}
