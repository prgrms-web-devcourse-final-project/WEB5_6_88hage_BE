package com.grepp.funfun.app.infra.auth.jwt;

import com.grepp.funfun.app.infra.error.exceptions.AuthApiException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    public CustomAccessDeniedHandler(
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.resolver = resolver;
    }

    @Override
    public void handle(HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {

        resolver.resolveException(
            request,
            response,
            null,
            new AuthApiException(ResponseCode.FORBIDDEN)
        );
    }
}