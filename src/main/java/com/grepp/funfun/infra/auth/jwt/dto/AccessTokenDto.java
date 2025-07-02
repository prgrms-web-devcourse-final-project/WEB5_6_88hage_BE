package com.grepp.funfun.infra.auth.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class AccessTokenDto {

    private String jti;
    private String token;
    private Long expires;
    
}
