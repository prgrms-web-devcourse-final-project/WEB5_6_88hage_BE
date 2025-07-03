package com.grepp.funfun.infra.auth.jwt;

import org.springframework.http.ResponseCookie;

public class TokenCookieFactory {
    public static ResponseCookie create(String name, String value, Long expires) {
        return ResponseCookie.from(name, value)
                   .maxAge(expires + 300) // Refresh 토큰 만료기간 보다 5분 길게
                   .path("/")
                   .httpOnly(true)             // HttpOnly
                   .secure(false)
                   .sameSite("Lax")// Secure
                   .build();
    }
    
    public static ResponseCookie createExpiredToken(String name) {
        return ResponseCookie.from(name, "")
                   .maxAge(0)
                   .path("/")
                   .httpOnly(true)             // HttpOnly
                   .secure(false)
                   .sameSite("Lax")// // Secure
                   .build();
    }
}
