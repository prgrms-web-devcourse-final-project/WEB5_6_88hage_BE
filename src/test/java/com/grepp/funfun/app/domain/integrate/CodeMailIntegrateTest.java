package com.grepp.funfun.app.domain.integrate;

import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class CodeMailIntegrateTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final String email = "mail-test@aaa.aaa";

    @BeforeEach
    void setup() {
        userRepository.save(User.builder()
            .email(email)
            .role(Role.ROLE_USER)
            .isVerified(true)
            .status(UserStatus.ACTIVE)
            .build());
    }

    @Test
    void sendCode_And_verifyCode_OK() {
        // 인증 메일 발송
        userService.sendCode(email);

        // Redis 에 저장된 코드 확인
        String code = (String) redisTemplate.opsForValue().get("auth-code:" + email);
        assertNotNull(code);

        // 인증 코드 검증
        userService.verifyCode(email, code);

        // 검증된 키가 Redis 에 저장되어 있는지 확인
        Boolean verified = redisTemplate.hasKey("auth-code:verified:" + email);
        assertTrue(verified);

        redisTemplate.delete("auth-cooldown:code:" + email);
        redisTemplate.delete("auth-code:verified:" + email);
    }

    @Test
    void sendCode_COOLDOWN_EX() {
        // 인증 메일 발송
        userService.sendCode(email);

        // cooldown 생성 확인
        Boolean hasCooldownKey = redisTemplate.hasKey("auth-cooldown:code:" + email);
        assertTrue(hasCooldownKey);

        // 재발송
        CommonException commonException = assertThrows(CommonException.class,
            () -> userService.sendCode(email));
        assertEquals(ResponseCode.TOO_FAST_VERIFY_REQUEST, commonException.getCode());

        redisTemplate.delete("auth-code:" + email);
        redisTemplate.delete("auth-cooldown:code:" + email);
    }

    @Test
    void verifyCode_INVALID_CODE_EX() {
        // 인증 메일 발송
        userService.sendCode(email);
        // 원래는 6자리의 랜덤 숫자
        String wrongCode = "0000000";

        CommonException commonException = assertThrows(CommonException.class,
            () -> userService.verifyCode(email, wrongCode));
        assertEquals(ResponseCode.INVALID_AUTH_CODE, commonException.getCode());

        redisTemplate.delete("auth-code:" + email);
        redisTemplate.delete("auth-cooldown:code:" + email);
    }
}
