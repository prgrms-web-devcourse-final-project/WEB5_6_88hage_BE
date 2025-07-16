package com.grepp.funfun.app.domain.user.controller;

import com.grepp.funfun.app.domain.auth.dto.payload.TokenResponse;
import com.grepp.funfun.app.domain.user.dto.payload.ChangePasswordRequest;
import com.grepp.funfun.app.domain.user.dto.payload.NicknameRequest;
import com.grepp.funfun.app.domain.user.dto.payload.OAuth2SignupRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserInfoRequest;
import com.grepp.funfun.app.domain.user.dto.payload.SignupRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserInfoResponse;
import com.grepp.funfun.app.domain.user.dto.payload.VerifyCodeRequest;
import com.grepp.funfun.app.domain.auth.vo.AuthToken;
import com.grepp.funfun.app.domain.auth.dto.TokenDto;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.infra.auth.jwt.JwtTokenProvider;
import com.grepp.funfun.app.infra.auth.jwt.TokenCookieFactory;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ApiResponse;
import com.grepp.funfun.app.infra.response.ResponseCode;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입을 진행 후 인증 메일을 발송합니다.")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody @Valid SignupRequest request) {
        String createdEmail = userService.create(request);
        return ResponseEntity.ok(ApiResponse.success(createdEmail));
    }

    @PatchMapping("/oauth2/signup")
    @Operation(summary = "OAuth2 회원가입", description = "소셜 로그인 대상의 추가 정보를 입력 받습니다.<br>액세스 토큰을 재발급합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> updateOAuth2User(@RequestBody @Valid OAuth2SignupRequest request, Authentication authentication, HttpServletResponse response) {
        TokenDto tokenDto = userService.updateOAuth2User(authentication, request);
        TokenCookieFactory.setAllAuthCookies(response, tokenDto);

        return ResponseEntity.ok(ApiResponse.success(TokenResponse.builder().
            accessToken(tokenDto.getAccessToken())
            .grantType(tokenDto.getGrantType())
            .expiresIn(tokenDto.getExpiresIn())
            .build()));
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 조회", description = "회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUser(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(userService.getUserInfo(email)));
    }

    @PutMapping("/info")
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> updateUser(@RequestBody @Valid UserInfoRequest request, Authentication authentication) {
        String email = authentication.getName();
        userService.updateUserInfo(email, request);
        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정했습니다."));
    }

    @PatchMapping
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴(비활성화) 합니다.<br>자동으로 로그아웃됩니다.")
    public ResponseEntity<ApiResponse<String>> unActiveUser(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1. 액세스 토큰 꺼내기
        String accessToken = jwtTokenProvider.resolveToken(request, AuthToken.ACCESS_TOKEN);

        if (accessToken == null) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        // 2. 토큰에서 accessTokenId 파싱
        Claims claims = jwtTokenProvider.getClaims(accessToken);
        String accessTokenId = claims.getId();

        String email = authentication.getName();
        userService.unActive(email, accessTokenId);

        SecurityContextHolder.clearContext();
        TokenCookieFactory.setAllExpiredCookies(response);

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴되었습니다."));
    }

    @PostMapping("/verify/signup")
    @Operation(summary = "회원가입 이메일 인증", description = "회원가입 이메일 인증을 진행합니다.<br>자동으로 로그인됩니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> verifySignup(@RequestParam("code") String code,  HttpServletResponse response) {
        TokenDto tokenDto = userService.verifySignupCode(code);
        TokenCookieFactory.setAllAuthCookies(response, tokenDto);

        return ResponseEntity.ok(ApiResponse.success(TokenResponse.builder().
            accessToken(tokenDto.getAccessToken())
            .grantType(tokenDto.getGrantType())
            .expiresIn(tokenDto.getExpiresIn())
            .build()));
    }

    @PostMapping("/send/signup/{email}")
    @Operation(summary = "회원가입 인증 메일 재발송", description = "회원가입 인증 메일을 재발송합니다.<br>쿨타임은 3분이며, 링크는 5분간 유효합니다.")
    public ResponseEntity<ApiResponse<String>> resendVerificationSignupMail(@PathVariable("email") String email) {
        userService.resendVerificationSignupEmail(email);
        return ResponseEntity.ok(ApiResponse.success(email));
    }

    @PostMapping("/send/code")
    @Operation(summary = "인증 코드 메일 발송", description = "인증 코드 메일을 발송합니다.<br>쿨타임은 3분이며, 인증 코드는 5분간 유효합니다.<br>인증 키는 10분간 유효합니다.")
    public ResponseEntity<ApiResponse<String>> sendCode(Authentication authentication) {
        String email = authentication.getName();
        userService.sendCode(email);
        return ResponseEntity.ok(ApiResponse.success("인증 메일을 발송했습니다."));
    }

    @PostMapping("/verify/code")
    @Operation(summary = "인증 코드 검증", description = "올바른 인증 코드인지 검증합니다.")
    public ResponseEntity<ApiResponse<String>> verifyCode(@RequestBody @Valid VerifyCodeRequest request, Authentication authentication) {
        String email = authentication.getName();
        userService.verifyCode(email, request.getCode());
        return ResponseEntity.ok(ApiResponse.success("인증이 완료되었습니다."));
    }

    @PatchMapping("/change/password")
    @Operation(summary = "비밀번호 변경", description = "인증 코드로 인증된 사용자에 한해서 비밀번호 변경을 진행합니다.")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody @Valid
        ChangePasswordRequest request, Authentication authentication) {
        String email = authentication.getName();
        userService.changePassword(email, request.getPassword());

        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경이 완료되었습니다."));
    }

    @PatchMapping("/change/nickname")
    @Operation(summary = "닉네임 변경", description = "닉네임 변경을 합니다.<br>액세스 토큰을 재발급합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> changeNickname(@RequestBody @Valid
    NicknameRequest request, Authentication authentication, HttpServletResponse response) {
        TokenDto tokenDto = userService.changeNickname(authentication, request.getNickname());
        TokenCookieFactory.setAllAuthCookies(response, tokenDto);

        return ResponseEntity.ok(ApiResponse.success(TokenResponse.builder().
            accessToken(tokenDto.getAccessToken())
            .grantType(tokenDto.getGrantType())
            .expiresIn(tokenDto.getExpiresIn())
            .build()));
    }

    @PostMapping("/verify/nickname")
    @Operation(summary = "닉네임 중복 검사", description = "중복된 닉네임인지 검증합니다.")
    public ResponseEntity<ApiResponse<String>> verifyNickname(@RequestBody @Valid NicknameRequest request) {
        userService.verifyNickname(request.getNickname());
        return ResponseEntity.ok(ApiResponse.success("사용 가능한 닉네임입니다."));
    }
}
