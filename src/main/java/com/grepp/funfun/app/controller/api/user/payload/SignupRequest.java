package com.grepp.funfun.app.controller.api.user.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grepp.funfun.app.model.auth.code.Role;
import com.grepp.funfun.app.model.user.code.Gender;
import com.grepp.funfun.app.model.user.entity.User;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

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

    @NotBlank(message = "닉네임은 필수입니다.")
    @Pattern(
        regexp = "^[가-힣a-zA-Z0-9]{2,10}$",
        message = "닉네임은 한글, 영문, 숫자만 사용할 수 있으며, 2자 이상 10자 이하로 입력해야 합니다."
    )
    private String nickname;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String tel;

    @NotNull(message = "나이는 필수입니다.")
    private int age;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @NotNull(message = "마케팅 수신 여부를 선택해주세요.")
    @JsonProperty("isMarketingAgreed")
    private Boolean isMarketingAgreed;

    public User toEntity() {
        User user = new User();

        user.setEmail(email);
        user.setNickname(nickname);
        user.setAddress(address);
        user.setTel(tel);
        user.setAge(age);
        user.setGender(gender);
        user.setIsMarketingAgreed(isMarketingAgreed);
        user.setRole(Role.ROLE_USER);
        user.setIsVerified(false);

        return user;
    }
}
