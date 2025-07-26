package com.grepp.funfun.app.domain.integrate;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grepp.funfun.app.domain.group.service.GroupHashtagService;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AutoCompleteIntegrateTest {

    @Autowired
    private GroupHashtagService groupHashtagService;

    //    // 값 저장
//    public void saveWord(String word) {
//        for (int i = 1; i <= word.length(); i++) {
//            String prefix = word.substring(0, i);
//            String key = "autocomplete:" + prefix;
//            redisTemplate.opsForSet().add(key, word);
//        }
//    }
//
//    // 자동 완성
//    public Set<String> getAutoCompleteWord(String prefix) {
//        String key = "autocomplete:" + prefix;
//        Set<Object> results = redisTemplate.opsForSet().members(key);
//
//        // 값이 없을 경우
//        if(results == null||results.isEmpty()) {
//            return Collections.emptySet();
//        }
//        return results.stream().map(Object::toString).collect(Collectors.toSet());
//    }

    @Test
    public void autoCompleteTestWithSet(){

        groupHashtagService.saveWord("테스트");

        String prefix = "테";

        Set<String> result = groupHashtagService.getAutoCompleteWord(prefix);

        assertTrue(result.contains("테스트"));
    }
}
