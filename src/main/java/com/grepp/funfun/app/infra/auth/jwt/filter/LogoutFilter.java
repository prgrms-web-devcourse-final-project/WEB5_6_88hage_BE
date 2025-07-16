package com.grepp.funfun.app.infra.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.funfun.app.domain.auth.vo.AuthToken;
import com.grepp.funfun.app.domain.auth.token.RefreshTokenService;
import com.grepp.funfun.app.infra.auth.jwt.JwtTokenProvider;
import com.grepp.funfun.app.infra.auth.jwt.TokenCookieFactory;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Value("${front-server.domain}")
    private String front;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);

        if(accessToken == null){
            filterChain.doFilter(request,response);
            return;
        }

        String path = request.getRequestURI();
        Claims claims  = jwtTokenProvider.getClaims(accessToken);

        if(path.equals("/api/auth/logout")){
            refreshTokenService.deleteByAccessTokenId(claims.getId());
            TokenCookieFactory.setAllExpiredCookies(response);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");

            ApiResponse<String> result = ApiResponse.success("로그아웃 성공");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            response.sendRedirect(front + "/");
            return;
        }

        filterChain.doFilter(request,response);
    }
}
