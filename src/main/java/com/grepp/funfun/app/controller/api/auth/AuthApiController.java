package com.grepp.funfun.app.controller.api.auth;

import com.grepp.funfun.app.controller.api.auth.payload.LoginRequest;
import com.grepp.funfun.app.controller.api.auth.payload.TokenResponse;
import com.grepp.funfun.app.model.auth.AuthService;
import com.grepp.funfun.app.model.auth.code.AuthToken;
import com.grepp.funfun.app.model.auth.dto.TokenDto;
import com.grepp.funfun.infra.auth.jwt.TokenCookieFactory;
import com.grepp.funfun.infra.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
        @RequestBody @Valid LoginRequest loginRequest,
        HttpServletResponse response
    ) {
        TokenDto tokenDto = authService.signin(loginRequest);
        
        ResponseCookie accessToken = TokenCookieFactory.create(AuthToken.ACCESS_TOKEN.name(),
            tokenDto.getAccessToken(), tokenDto.getRefreshExpiresIn());
        ResponseCookie refreshToken = TokenCookieFactory.create(AuthToken.REFRESH_TOKEN.name(),
            tokenDto.getRefreshToken(), tokenDto.getRefreshExpiresIn());
        
        response.addHeader("Set-Cookie", accessToken.toString());
        response.addHeader("Set-Cookie", refreshToken.toString());
        
        return ResponseEntity.ok(ApiResponse.success(TokenResponse.builder().
                                                         accessToken(tokenDto.getAccessToken())
                                                         .grantType(tokenDto.getGrantType())
                                                         .expiresIn(tokenDto.getExpiresIn())
                                                         .build()));
    }
}
