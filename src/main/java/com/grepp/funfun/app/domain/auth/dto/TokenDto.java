package com.grepp.funfun.app.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private String atId;
    private String grantType;
    private Long expiresIn;
    private Long refreshExpiresIn;
}
