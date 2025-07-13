package com.grepp.funfun.app.domain.contact.dto.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactRequest {
    @NotBlank(message = "문의 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "문의 내용은 필수입니다.")
    private String content;
}
