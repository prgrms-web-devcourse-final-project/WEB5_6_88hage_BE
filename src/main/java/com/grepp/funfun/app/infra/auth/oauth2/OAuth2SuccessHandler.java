package com.grepp.funfun.app.infra.auth.oauth2;

import com.grepp.funfun.app.domain.auth.dto.TokenDto;
import com.grepp.funfun.app.domain.auth.service.AuthService;
import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.preference.repository.ContentPreferenceRepository;
import com.grepp.funfun.app.domain.preference.repository.GroupPreferenceRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.entity.UserInfo;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.auth.jwt.TokenCookieFactory;
import com.grepp.funfun.app.infra.auth.oauth2.user.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ContentPreferenceRepository contentPreferenceRepository;
    private final GroupPreferenceRepository groupPreferenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${front-server.domain}")
    String front;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getEmail();

        log.info("OAuth2 로그인 성공: email={}", email);

        boolean isExistingUser = userRepository.existsByEmail(email);

        if (!isExistingUser) {
            User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(oAuth2User.getProviderId()))
                .role(Role.ROLE_GUEST)
                .isVerified(true)
                .info(UserInfo.builder()
                    .email(email)
                    .build())
                .build();
            userRepository.save(newUser);
            log.info("OAuth2 사용자 기본 정보 저장: {}", email);

            TokenDto tokenDto = authService.processTokenLogin(email, "", newUser.getRole().name(), false);
            TokenCookieFactory.setAllAuthCookies(response, tokenDto);

            // NOTE : 프론트 경로로 변경 필요
            //  OAuth2 추가 회원 가입 페이지로
            getRedirectStrategy().sendRedirect(request, response, front+ "/api/auth/oauth2");
        } else {
            User user = userRepository.findById(email).orElse(null);

            if (user != null) {
                TokenDto tokenDto = authService.processTokenLogin(email, user.getNickname() ,user.getRole().name(), false);

                TokenCookieFactory.setAllAuthCookies(response, tokenDto);

                if (user.getRole().equals(Role.ROLE_GUEST)) {
                    // NOTE : 프론트 경로로 변경 필요
                    //  OAuth2 추가 회원 가입 페이지로
                    getRedirectStrategy().sendRedirect(request, response, front+ "/api/auth/oauth2");
                    return;
                }

                if (groupPreferenceRepository.findByUserEmail(email).isEmpty() || contentPreferenceRepository.findByUserEmail(email).isEmpty()) {
                    // NOTE : 프론트 경로로 변경 필요
                    //  유저 취향 설정 페이지로
                    getRedirectStrategy().sendRedirect(request, response, front+ "/user/preference");
                } else {
                    getRedirectStrategy().sendRedirect(request, response, front+ "/");
                }
            }
        }
    }
}
