package com.grepp.funfun.app.domain.group.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

@ExtendWith(MockitoExtension.class)
public class AutoCompleteTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @InjectMocks
    private GroupHashtagService groupHashtagService;

    @Test
    public void autoComplete(){
        //GIVEN
        String prefix = "대";
        String key = "autocomplete:" + prefix;
        Set<Object> mockResults = Set.of("대한민국", "대전");

        //WHEN
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(key)).thenReturn(mockResults);

        Set<String> result = groupHashtagService.getAutoCompleteWord(prefix);

        //THEN
        assertThat(result).containsExactlyInAnyOrder("대한민국", "대전");
    }

}
