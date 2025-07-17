package com.grepp.funfun.app.domain.admin.dto.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminContactAnswerRequest {
    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String answer;
}
