package com.grepp.funfun.app.infra.auth.jwt.filter;

import com.grepp.funfun.app.domain.auth.vo.AuthToken;
import com.grepp.funfun.app.domain.auth.token.RefreshTokenService;
import com.grepp.funfun.app.infra.auth.jwt.JwtTokenProvider;
import com.grepp.funfun.app.infra.auth.jwt.TokenCookieFactory;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {
    
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;
    
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
        
        if(path.equals("/auth/logout")){
            refreshTokenService.deleteByAccessTokenId(claims.getId());
            TokenCookieFactory.setAllExpiredCookies(response);
            // NOTE: 프론트 측 URI 로 변경 필요
            response.sendRedirect("/");
        }
        
        filterChain.doFilter(request,response);
    }
}
