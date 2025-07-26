package com.grepp.funfun.app.domain.group.service;

import com.grepp.funfun.app.domain.group.repository.GroupHashtagRepository;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupHashtagService {

    private final GroupHashtagRepository groupHashtagRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 값 저장
    public void saveWord(String word){
        String key = "autocomplete:zset";
        redisTemplate.opsForZSet().add(key, word, 0);
    }

    // 자동 완성
    public Set<String> getAutoCompleteWord(String prefix){
        String key = "autocomplete:zset";
        Set<String> allWords = redisTemplate.opsForZSet().range(key, 0, -1);

        if (allWords == null || allWords.isEmpty()) {
            return Collections.emptySet();
        }

        return allWords.stream()
            .filter(word -> word.startsWith(prefix))
            .collect(Collectors.toSet());
    }

    public void delete(final Long id) {
        groupHashtagRepository.deleteById(id);
    }

}