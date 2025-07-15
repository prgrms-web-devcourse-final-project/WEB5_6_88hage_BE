package com.grepp.funfun.app.infra.auth.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${front-server.domain}")
    String front;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {

        String errorCode = "oauth2_authentication_error"; // 기본값
        String description = "OAuth2 인증 중 알 수 없는 오류가 발생했습니다.";

        if (exception instanceof OAuth2AuthenticationException oae) {
            errorCode = oae.getError().getErrorCode();
            description = oae.getError().getDescription();
            log.warn("OAuth2 로그인 실패 - code: {}, description: {}", errorCode, description);
        } else {
            log.warn("OAuth2 로그인 실패 - 예외: {}", exception.getMessage());
        }

        // NOTE : 프론트 경로로 변경 필요
        getRedirectStrategy().sendRedirect(request, response, front + "/login?error=" + errorCode + "&from=oauth2");
    }
}
