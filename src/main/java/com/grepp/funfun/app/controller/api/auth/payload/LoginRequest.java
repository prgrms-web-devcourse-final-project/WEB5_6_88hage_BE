package com.grepp.funfun.app.controller.api.auth.payload;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
