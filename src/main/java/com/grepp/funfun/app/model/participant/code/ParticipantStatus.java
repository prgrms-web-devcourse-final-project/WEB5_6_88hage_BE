package com.grepp.funfun.app.model.participant.code;

import lombok.Getter;

@Getter
public enum ParticipantStatus {

    PENDING("대기"),
    APPROVED("수락"),
    REJECTED("거절"),
    LEAVE("모임 나감"),
    GROUP_COMPLETE("모임 완료됨"),
    GROUP_KICKOUT("모임 강제퇴장"),
    GROUP_DELETED("모임 삭제됨"),// 모임 삭제로 인한 취소
    GROUP_CANCELED("모임 취소됨");

    private final String koreanName;

    ParticipantStatus(String koreanName) {
        this.koreanName = koreanName;
    }

}
