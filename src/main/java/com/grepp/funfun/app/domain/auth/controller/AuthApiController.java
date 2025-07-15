package com.grepp.funfun.app.domain.auth.controller;

import com.grepp.funfun.app.domain.auth.payload.LoginRequest;
import com.grepp.funfun.app.domain.auth.payload.TokenResponse;
import com.grepp.funfun.app.domain.auth.service.AuthService;
import com.grepp.funfun.app.domain.auth.dto.TokenDto;
import com.grepp.funfun.app.domain.preference.service.PreferenceService;
import com.grepp.funfun.app.infra.auth.jwt.TokenCookieFactory;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PreferenceService preferenceService;
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 성공 시 AccessToken 쿠키와 RefreshToken 쿠키를 발급합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
        @RequestBody @Valid LoginRequest loginRequest,
        HttpServletResponse response
    ) {
        TokenDto tokenDto = authService.signin(loginRequest);
        TokenCookieFactory.setAllAuthCookies(response, tokenDto);

        // 취향 설정을 하지 않은 사용자
        if (!preferenceService.hasPreferences(loginRequest.getEmail())) {
            return ResponseEntity.ok(ApiResponse.error(ResponseCode.USER_PREFERENCE_NOT_SET));
        }

        return ResponseEntity.ok(ApiResponse.success(TokenResponse.builder().
                                                         accessToken(tokenDto.getAccessToken())
                                                         .grantType(tokenDto.getGrantType())
                                                         .expiresIn(tokenDto.getExpiresIn())
                                                         .build()));
    }
}
