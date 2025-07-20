package com.grepp.funfun.app.domain.group.service;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
public class ViewCountServiceTest {


    @Mock private RedisTemplate<String, Object> redisTemplate;
    // 가짜 valueOperations 객체가 리턴되도록 설정
    @Mock private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private GroupService groupService;

    @Test
    public void viewCount() {
        // GIVEN
        Long groupId = 1L;
        String email = "test@example.com";
        String redisKey = "group:viewCount:" + groupId + ":user:" + email;
        String countKey = "group:" + groupId + ":viewCount";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        // WHEN
        groupService.increaseViewCountIfNotCounted(groupId, email);

        // THEN
        verify(redisTemplate).hasKey(redisKey);
        verify(valueOperations).increment(countKey);
        verify(valueOperations).set(eq(redisKey), eq("1"), eq(Duration.ofMinutes(10)));
    }
}
