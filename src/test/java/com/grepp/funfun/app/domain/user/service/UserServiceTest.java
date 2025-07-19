package com.grepp.funfun.app.domain.user.service;

import com.grepp.funfun.app.domain.user.dto.payload.SignupRequest;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.mail.MailTemplate;
import com.grepp.funfun.app.infra.mail.SmtpDto;
import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private MailTemplate mailTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    private String email = "test@test.test";
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email(email)
            .build();
    }

    @Test
    void createUser_OK() {
        // given
        SignupRequest request = SignupRequest.builder()
            .email(email)
            .nickname("nickname")
            .password("password")
            .build();

        // when
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(userRepository.existsByNickname("nickname")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ReflectionTestUtils.setField(userService, "domain", "domain");
        String result = userService.create(request);

        // then
        assertEquals(email, result);
        verify(userRepository).save(any(User.class));
        verify(valueOperations).set(startsWith("signup:"), eq(email), eq(Duration.ofMinutes(5)));
        verify(mailTemplate).send(any(SmtpDto.class));
    }

    @Test
    void sendCode_OK() {
        // given

        // when
        when(userRepository.findById(email)).thenReturn(Optional.of(user));
        when(redisTemplate.hasKey("auth-cooldown:code:" + email)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        userService.sendCode(email);

        // then
        verify(valueOperations).set(eq("auth-code:" + email), any(String.class), eq(Duration.ofMinutes(5)));
        verify(valueOperations).set("auth-cooldown:code:" + email,  "true", Duration.ofMinutes(3));
        verify(mailTemplate).send(any(SmtpDto.class));
    }

    @Test
    void sendCode_COOLDOWN_EX() {
        // given

        // when
        when(userRepository.findById(email)).thenReturn(Optional.of(user));
        when(redisTemplate.hasKey("auth-cooldown:code:" + email)).thenReturn(true);

        // when, then
        assertThrows(CommonException.class, () -> userService.sendCode(email));
    }

    @Test
    void verifyCode_OK() {
        // given
        String correctCode = "123456";

        // when
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth-code:" + email)).thenReturn(correctCode);
        userService.verifyCode(email, correctCode);

        // then
        verify(redisTemplate).delete("auth-code:" + email);
        verify(valueOperations).set("auth-code:verified:" + email, "true", Duration.ofMinutes(10));
    }

    @Test
    void verifyCode_FAIL_WRONG_CODE_EX() {
        // given

        // when, then
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth-code:" + email)).thenReturn("654321");
        assertThrows(CommonException.class, () -> userService.verifyCode(email, "123456"));
    }

    @Test
    void verifyCode_FAIL_CODE_NOT_EXIST_EX() {
        // given

        // when, then
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("auth-code:" + email)).thenReturn(null);
        assertThrows(CommonException.class, () -> userService.verifyCode(email, "123456"));
    }

    @Test
    void changePassword_OK() {
        // given

        // when
        when(userRepository.findById(email)).thenReturn(Optional.of(user));
        when(redisTemplate.hasKey("auth-code:verified:" + email)).thenReturn(true);
        userService.changePassword(email, "new-password");

        // then
        verify(redisTemplate).delete("auth-code:verified:" + email);
        verify(redisTemplate).delete("auth-cooldown:code:" + email);
    }

    @Test
    void changePassword_NOT_VERIFY_EX() {
        // given

        // when
        when(userRepository.findById(email)).thenReturn(Optional.of(user));
        when(redisTemplate.hasKey("auth-code:verified:" + email)).thenReturn(false);

        // then
        assertThrows(CommonException.class, () -> userService.changePassword(email, "new-password"));
    }
}