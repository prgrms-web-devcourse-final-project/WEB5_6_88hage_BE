package com.grepp.funfun.app.domain.integrate;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;


// 테스트 클래스
@SpringBootTest
@Transactional
public class AutoCompleteWithZSetIntegrateTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String REDIS_KEY = "autocomplete:zset";

    @BeforeEach
    void init() {
        redisTemplate.opsForZSet().add(REDIS_KEY, "아이유", 0);
        redisTemplate.opsForZSet().add(REDIS_KEY, "아이스크림", 0);
        redisTemplate.opsForZSet().add(REDIS_KEY, "아메리카노", 0);
        redisTemplate.opsForZSet().add(REDIS_KEY, "사과", 0);
        redisTemplate.opsForZSet().add(REDIS_KEY, "바나나", 0);
    }

    @AfterEach
    void delete() {
        redisTemplate.delete(REDIS_KEY);
    }

    @Test
    public void autoCompleteIntegrateTestWithZSet() {
        // GIVEN
        String prefix = "아";

        // WHEN - 모든 단어
        Set<String> allWords = redisTemplate.opsForZSet().range(REDIS_KEY, 0, -1);

        assert allWords != null;
        Set<String> filteredResults = allWords.stream()
            .filter(word -> word.startsWith(prefix))
            .collect(Collectors.toSet());

        // THEN
        assertThat(filteredResults).hasSize(3);
        assertThat(filteredResults).contains("아이유", "아이스크림", "아메리카노");
        assertThat(filteredResults).doesNotContain("사과", "바나나");
    }

}