package com.grepp.funfun.app.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthUserDTO {
    private String role;
    private String name;
    private String email;
    private String provider;
    private String providerId;
}
