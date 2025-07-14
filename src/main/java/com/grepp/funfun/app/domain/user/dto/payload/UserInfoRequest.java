package com.grepp.funfun.app.domain.user.dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.grepp.funfun.app.domain.user.vo.Gender;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.Data;

@Data
public class UserInfoRequest {

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
}
