package com.grepp.funfun.infra.auth.oauth2;

import com.grepp.funfun.app.model.auth.AuthService;
import com.grepp.funfun.app.model.auth.code.Role;
import com.grepp.funfun.app.model.auth.dto.TokenDto;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.entity.UserInfo;
import com.grepp.funfun.app.model.user.repository.UserInfoRepository;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.auth.jwt.TokenCookieFactory;
import com.grepp.funfun.infra.auth.oauth2.user.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getEmail();

        log.info("OAuth2 로그인 성공: email={}", email);

        boolean isExistingUser = userRepository.existsByEmail(email);

        if (!isExistingUser) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(oAuth2User.getProviderId()));
            newUser.setRole(Role.ROLE_GUEST);
            newUser.setIsVerified(true);
            UserInfo userInfo = new UserInfo();
            userInfo.setEmail(email);
            userInfoRepository.save(userInfo);
            newUser.setInfo(userInfo);

            userRepository.save(newUser);
            log.info("OAuth2 사용자 기본 정보 저장: {}", email);

            TokenDto tokenDto = authService.processTokenSignin(email, newUser.getRole().name(), false);

            TokenCookieFactory.setAllAuthCookies(response, tokenDto);

            // NOTE : 프론트 측 URI 로 변경 필요
            response.sendRedirect("/users/oauth2/signup");

        } else {
            User user = userRepository.findById(email).orElse(null);
            if (user != null) {
                TokenDto tokenDto = authService.processTokenSignin(email, user.getRole().name(), false);

                TokenCookieFactory.setAllAuthCookies(response, tokenDto);

                if (user.getGroupPreferences() == null || user.getContentPreferences() == null) {
                    // NOTE : 프론트 측 URI 로 변경 필요
                    response.sendRedirect("/users/preference");
                } else {
                    response.sendRedirect("/");
                }
            }
        }
    }
}
