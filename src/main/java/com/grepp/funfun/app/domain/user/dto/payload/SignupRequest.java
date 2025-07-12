package com.grepp.funfun.app.domain.user.dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.user.vo.Gender;
import com.grepp.funfun.app.domain.user.entity.User;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @NotNull(message = "위도는 필수입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수입니다.")
    private Double longitude;

    @NotBlank(message = "생년월일은 필수입니다.")
    private String birthDate;

    @AssertTrue(message = "생년월일은 yyyyMMdd 형식의 유효한 날짜여야 합니다.")
    public boolean isBirthDate() {
        try {
            LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

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
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setBirthDate(birthDate);
        user.setGender(gender);
        user.setIsMarketingAgreed(isMarketingAgreed);
        user.setRole(Role.ROLE_USER);
        user.setIsVerified(false);

        return user;
    }
}
