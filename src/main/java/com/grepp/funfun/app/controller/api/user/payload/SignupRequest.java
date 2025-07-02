package com.grepp.funfun.app.controller.api.user.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grepp.funfun.app.model.auth.code.Role;
import com.grepp.funfun.app.model.user.code.Gender;
import com.grepp.funfun.app.model.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignupRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
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
