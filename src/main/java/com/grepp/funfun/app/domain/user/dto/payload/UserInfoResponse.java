package com.grepp.funfun.app.domain.user.dto.payload;

import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.vo.Gender;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {

    private String email;

    private String nickname;

    private String address;

    private Double latitude;

    private Double longitude;

    private String birthDate;

    private Gender gender;

    private Boolean isMarketingAgreed;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
            .email(user.getEmail())
            .nickname(user.getNickname())
            .address(user.getAddress())
            .latitude(user.getLatitude())
            .longitude(user.getLongitude())
            .birthDate(user.getBirthDate())
            .gender(user.getGender())
            .isMarketingAgreed(user.getIsMarketingAgreed())
            .build();
    }
}
