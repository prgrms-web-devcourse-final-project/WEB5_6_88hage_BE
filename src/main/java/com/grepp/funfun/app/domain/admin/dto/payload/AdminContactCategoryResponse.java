package com.grepp.funfun.app.domain.admin.dto.payload;

import com.grepp.funfun.app.domain.contact.vo.ContactCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminContactCategoryResponse {
    private String code;
    private String label;

    public static AdminContactCategoryResponse from(ContactCategory category) {
        return AdminContactCategoryResponse.builder()
                .code(category.name())
                .label(category.getKoreanName())
                .build();
    }
}
