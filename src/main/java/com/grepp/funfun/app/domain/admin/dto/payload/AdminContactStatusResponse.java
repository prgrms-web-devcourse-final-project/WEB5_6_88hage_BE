package com.grepp.funfun.app.domain.admin.dto.payload;

import com.grepp.funfun.app.domain.contact.vo.ContactStatus;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AdminContactStatusResponse {
    private String status;
    private String koreanName;

    public static AdminContactStatusResponse from(ContactStatus status) {
        return AdminContactStatusResponse.builder()
                .status(status.name())
                .koreanName(status.getKoreanName())
                .build();
    }

}
