package com.grepp.funfun.app.infra.auth.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

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

        ApiResponse<Void> errorResponse = ApiResponse.error(ResponseCode.OAUTH2_AUTHENTICATION_FAILED, description);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
