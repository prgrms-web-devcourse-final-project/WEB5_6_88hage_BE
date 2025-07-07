package com.grepp.funfun.app.controller.api.user.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NicknameRequest {
    @NotBlank(message = "닉네임은 필수입니다.")
    @Pattern(
        regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
        message = "닉네임은 한글, 영문, 숫자만 사용할 수 있으며, 2자 이상 10자 이하로 입력해야 합니다."
    )
    private String nickname;
}
