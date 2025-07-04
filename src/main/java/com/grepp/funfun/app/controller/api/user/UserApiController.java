package com.grepp.funfun.app.controller.api.user;

import com.grepp.funfun.app.controller.api.auth.payload.TokenResponse;
import com.grepp.funfun.app.controller.api.user.payload.ChangePasswordRequest;
import com.grepp.funfun.app.controller.api.user.payload.OAuth2SignupRequest;
import com.grepp.funfun.app.controller.api.user.payload.SignupRequest;
import com.grepp.funfun.app.controller.api.user.payload.VerifyCodeRequest;
import com.grepp.funfun.app.model.auth.code.AuthToken;
import com.grepp.funfun.app.model.auth.dto.TokenDto;
import com.grepp.funfun.app.model.user.dto.UserDTO;
import com.grepp.funfun.app.model.user.service.UserService;
import com.grepp.funfun.infra.auth.jwt.TokenCookieFactory;
import com.grepp.funfun.infra.response.ApiResponse;
import com.grepp.funfun.util.ReferencedException;
import com.grepp.funfun.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserApiController {

    private final UserService userService;

    public UserApiController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "email") final String email) {
        return ResponseEntity.ok(userService.get(email));
    }

    @PostMapping
    @Operation(summary = "회원가입", description = "회원가입을 진행 후 인증 메일을 발송합니다.")
    public ResponseEntity<ApiResponse<String>> createUser(@RequestBody @Valid SignupRequest request) {
        String createdEmail = userService.create(request);
        return ResponseEntity.ok(ApiResponse.success(createdEmail));
    }

    @PatchMapping("/oauth2")
    @Operation(summary = "OAuth2 회원가입", description = "소셜 로그인 대상의 추가 정보를 입력 받습니다.")
    public ResponseEntity<ApiResponse<String>> updateOAuth2User(@RequestBody @Valid OAuth2SignupRequest request, Authentication authentication) {
        String email = authentication.getName();
        userService.updateOAuth2User(email, request);
        return ResponseEntity.ok(ApiResponse.success(email));
    }

    @PutMapping("/{email}")
    public ResponseEntity<String> updateUser(@PathVariable(name = "email") final String email,
            @RequestBody @Valid final UserDTO userDTO) {
        userService.update(email, userDTO);
        return ResponseEntity.ok('"' + email + '"');
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "email") final String email) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(email);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify/signup")
    @Operation(summary = "회원가입 이메일 인증", description = "회원가입 이메일 인증을 진행합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> verifySignup(@RequestParam("code") String code,  HttpServletResponse response) {
        TokenDto tokenDto = userService.verifySignupCode(code);

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
}
