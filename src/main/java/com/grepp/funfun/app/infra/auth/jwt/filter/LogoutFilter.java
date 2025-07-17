package com.grepp.funfun.app.infra.auth.jwt.filter;

import com.grepp.funfun.app.domain.auth.token.RefreshTokenService;
import com.grepp.funfun.app.domain.auth.vo.AuthToken;
import com.grepp.funfun.app.infra.auth.jwt.JwtTokenProvider;
import com.grepp.funfun.app.infra.auth.jwt.TokenCookieFactory;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${front-server.domain}")
    String front;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.equals("/api/auth/logout")) {
            String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);

            if (accessToken != null) {
                Claims claims = jwtTokenProvider.getClaims(accessToken);
                refreshTokenService.deleteByAccessTokenId(claims.getId());
            }

            SecurityContextHolder.clearContext();
            TokenCookieFactory.setAllExpiredCookies(response);

            response.sendRedirect(front + "/");
            return;
        }

        filterChain.doFilter(request,response);
    }
}
