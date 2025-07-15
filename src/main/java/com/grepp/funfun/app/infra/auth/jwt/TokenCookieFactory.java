package com.grepp.funfun.app.infra.auth.jwt;

import com.grepp.funfun.app.domain.auth.vo.AuthToken;
import com.grepp.funfun.app.domain.auth.dto.TokenDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;

public class TokenCookieFactory {

    public static ResponseCookie create(String name, String value, Long expires) {
        return ResponseCookie.from(name, value)
            .maxAge(expires + 300) // Refresh 토큰 만료기간 보다 5분 길게
            .path("/")
            .httpOnly(true) // HttpOnly
            .secure(false)
            .sameSite("Lax") // Secure
            .build();
    }

    public static ResponseCookie createExpiredToken(String name) {
        return ResponseCookie.from(name, "")
            .maxAge(0)
            .path("/")
            .httpOnly(true) // HttpOnly
            .secure(false)
            .sameSite("Lax") // Secure
            .build();
    }

    public static void setAllAuthCookies(HttpServletResponse response, TokenDto tokenDto) {
        ResponseCookie accessToken = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            tokenDto.getAccessToken(), tokenDto.getRefreshExpiresIn());
        ResponseCookie refreshToken = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
            tokenDto.getRefreshToken(), tokenDto.getRefreshExpiresIn());

        response.addHeader("Set-Cookie", accessToken.toString());
        response.addHeader("Set-Cookie", refreshToken.toString());
    }

    public static void setAllExpiredCookies(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        ResponseCookie expiredAccessToken = TokenCookieFactory.createExpiredToken(
            AuthToken.ACCESS_TOKEN.name());
        ResponseCookie expiredRefreshToken = TokenCookieFactory.createExpiredToken(
            AuthToken.REFRESH_TOKEN.name());

        response.addHeader("Set-Cookie", expiredAccessToken.toString());
        response.addHeader("Set-Cookie", expiredRefreshToken.toString());
    }
}
