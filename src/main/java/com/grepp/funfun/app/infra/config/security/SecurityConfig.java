package com.grepp.funfun.app.infra.config.security;

import com.grepp.funfun.app.infra.auth.oauth2.CustomOauth2UserService;
import com.grepp.funfun.app.infra.auth.jwt.JwtAuthenticationEntryPoint;
import com.grepp.funfun.app.infra.auth.jwt.filter.JwtAuthenticationFilter;
import com.grepp.funfun.app.infra.auth.jwt.filter.JwtExceptionFilter;
import com.grepp.funfun.app.infra.auth.oauth2.OAuth2FailureHandler;
import com.grepp.funfun.app.infra.auth.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;
    private final CustomOauth2UserService customOauth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .oauth2Login((oauth) -> oauth
                .userInfoEndpoint(endpoint -> endpoint.userService(customOauth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .logout(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                (requests) -> requests
                    .requestMatchers("/favicon.ico", "/img/**", "/js/**", "/css/**").permitAll()
                    // 스웨거
                    .requestMatchers("/", "/error", "/.well-known/**", "/swagger-ui/**", "/swagger-ui.html/**", "/v3/**").permitAll()

                    // 로그인/로그아웃
                    .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()

                    // 모임
                    .requestMatchers(HttpMethod.GET, "/api/groups/search", "/api/groups/{groupId:[0-9]+}", "/api/groupHashtags/complete").permitAll()
                    .requestMatchers("/api/groups/**").hasRole("USER")

                    // 참가자
                    .requestMatchers("/api/participants/**").hasRole("USER")

                    // 채팅
                    .requestMatchers("/api/chatRooms/**", "/api/chats/**").hasRole("USER")

                    // 모임 해시태그
                    .requestMatchers(HttpMethod.GET, "/api/groupHashtags/complete").permitAll()
                    .requestMatchers("/api/groupHashtags/save").hasRole("ADMIN")

                    // 관리자
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")

                    // 컨텐츠
                    .requestMatchers(HttpMethod.GET, "/api/contents/**").permitAll()

                    // 추천
                    .requestMatchers("/api/recommend/**", "/api/chatBot/**").permitAll()

                    // 캘린더
                    .requestMatchers("/api/calendars/**").hasRole("USER")

                    // 문의
                    .requestMatchers("/api/contacts/**").hasRole("USER")

                    // 취향
                    .requestMatchers("/api/preferences/**").hasAnyRole("GUEST", "USER")

                    // 신고
                    .requestMatchers("/api/reports/**").hasRole("USER")

                    // 팔로우
                    .requestMatchers("/api/follows/**").hasRole("USER")

                    // 유저
                    .requestMatchers("/api/users/signup", "/api/users/verify/**", "/api/users/send/**", "/api/users/change/password/**").permitAll()
                    // ROLE_GUEST 만 OAuth2 회원가입 페이지 허용
                    .requestMatchers("/api/users/oauth2").hasRole("GUEST")
                    .requestMatchers("/api/users/info", "/api/users/change/nickname").hasRole("USER")
                    .requestMatchers(HttpMethod.PATCH, "/api/users").hasAnyRole("GUEST", "USER")
                    .requestMatchers("/api/users/coordinate").hasAnyRole("ADMIN", "USER")

                    // 유저 인포
                    .requestMatchers(HttpMethod.GET, "/api/userInfos/**").permitAll()
                    .requestMatchers(HttpMethod.PUT,"/api/userInfos").hasRole("USER")

//                    .requestMatchers("/api/**").permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(jwtAuthenticationEntryPoint,
                    new AntPathRequestMatcher("/api/**"))
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
