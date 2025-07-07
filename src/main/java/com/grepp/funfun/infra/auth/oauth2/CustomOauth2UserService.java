package com.grepp.funfun.infra.auth.oauth2;

import com.grepp.funfun.app.model.auth.code.Role;
import com.grepp.funfun.app.model.user.dto.OAuthUserDTO;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.auth.oauth2.user.CustomOAuth2User;
import com.grepp.funfun.infra.auth.oauth2.user.GoogleOAuth2UserInfo;
import com.grepp.funfun.infra.auth.oauth2.user.NaverOAuth2UserInfo;
import com.grepp.funfun.infra.auth.oauth2.user.OAuth2UserInfo;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 사용자 로드: {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = null;

        if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")){
            oAuth2UserInfo = new NaverOAuth2UserInfo((Map)oAuth2User.getAttributes().get("response"));
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();

        // 일반 회원가입 계정인지 검사
        Optional<User> existing = userRepository.findById(email);
        if (existing.isPresent()) {
            User user = existing.get();
            if (!passwordEncoder.matches(providerId, user.getPassword())) {
                log.warn("이미 존재하는 이메일입니다: {}", email);
                throw new OAuth2AuthenticationException(
                    new OAuth2Error("existing_email", "이미 회원가입된 이메일입니다.", null)
                );
            }
        }

        OAuthUserDTO userDto = new OAuthUserDTO();
        userDto.setName(name);
        userDto.setEmail(email);
        userDto.setRole(Role.ROLE_GUEST.name());
        userDto.setProvider(provider);
        userDto.setProviderId(providerId);

        return new CustomOAuth2User(userDto);
    }
}
