package com.grepp.funfun.app.controller.api.user.payload;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$",
        message = "8~20자 영문, 숫자, 특수문자 조합이어야 합니다"
    )
    private String password;

    private String confirmPassword;

    @AssertTrue(message = "비밀번호가 서로 일치하지 않습니다.")
    public boolean isConfirmPassword() {
        return password.equals(confirmPassword);
    }
}
