package com.grepp.funfun.app.domain.auth.service;

import com.grepp.funfun.app.domain.auth.domain.Principal;
import com.grepp.funfun.app.domain.auth.payload.LoginRequest;
import com.grepp.funfun.app.domain.auth.dto.TokenDto;
import com.grepp.funfun.app.domain.auth.token.RefreshTokenService;
import com.grepp.funfun.app.domain.auth.token.UserBlackListRepository;
import com.grepp.funfun.app.domain.auth.token.entity.RefreshToken;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.auth.jwt.JwtTokenProvider;
import com.grepp.funfun.app.infra.auth.jwt.dto.AccessTokenDto;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserBlackListRepository userBlackListRepository;
    private final UserRepository userRepository;

    public TokenDto signin(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword());

        // loadUserByUsername + password 검증 후 인증 객체 반환
        // 인증 실패 시: AuthenticationException 발생
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        User user = userRepository.findById(loginRequest.getEmail()).get();

        // 이메일 인증이 되지 않은 사용자
        if (!user.getIsVerified()) {
            throw new CommonException(ResponseCode.USER_NOT_VERIFY);
        }

        // 영구 정지된 사용자
        if (user.getStatus() == UserStatus.BANNED) {
            throw new CommonException(ResponseCode.USER_BANNED, "정지 사유: " + user.getDueReason());
        }

        // 일시 정지된 사용자
        if (user.getStatus() == UserStatus.SUSPENDED) {
            if (user.getDueDate() != null && !user.getDueDate().isAfter(java.time.LocalDate.now())) {
                user.setStatus(UserStatus.ACTIVE);
                user.setDueDate(null);
                user.setSuspendDuration(null);
                user.setDueReason(null);
                userRepository.save(user);
            } else {
                throw new CommonException(ResponseCode.USER_SUSPENDED, "정지 사유: " + user.getDueReason());
            }
        }

        // 비활성화한 사용자 (Soft Delete)
        if (!user.getActivated()) {
            throw new CommonException(ResponseCode.USER_INACTIVE);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String roles = String.join(",",
            authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return processTokenSignin(authentication.getName(), user.getNickname(), roles, loginRequest.isRememberMe());
    }

    @Transactional
    public TokenDto processTokenSignin(String email, String nickname, String roles, boolean rememberMe) {
        // black list 에 있다면 해제
        userBlackListRepository.deleteById(email);

        long refreshTokenExpiration = jwtTokenProvider.getRefreshTokenExpiration(rememberMe);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        AccessTokenDto accessToken = jwtTokenProvider.generateAccessToken(email, nickname, roles);
        RefreshToken refreshToken = refreshTokenService.saveWithAtId(accessToken.getJti(), refreshTokenExpiration);

        return TokenDto.builder()
            .accessToken(accessToken.getToken())
            .atId(accessToken.getJti())
            .refreshToken(refreshToken.getToken())
            .grantType("Bearer")
            .refreshExpiresIn(refreshTokenExpiration)
            .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
            .build();
    }

    @Transactional
    public TokenDto reissueAccessToken(Authentication authentication, String nickname, String roles) {
        Principal principal = (Principal) authentication.getPrincipal();
        String accessToken = principal.getAccessToken().orElseThrow(() -> new CommonException(ResponseCode.UNAUTHORIZED));
        String atId = jwtTokenProvider.getClaims(accessToken).getId();

        // 새로운 닉네임이나 권한으로 Access Token 재발급
        AccessTokenDto newAccessToken = jwtTokenProvider.generateAccessToken(authentication.getName(), nickname, roles);
        // 기존 Refresh Token 재사용
        RefreshToken refreshToken = refreshTokenService.renewingToken(atId, newAccessToken.getJti());

        return TokenDto.builder()
            .accessToken(newAccessToken.getToken())
            .atId(newAccessToken.getJti())
            .refreshToken(refreshToken.getToken())
            .grantType("Bearer")
            .refreshExpiresIn(refreshToken.getTtl())
            .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
            .build();
    }
}
