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

    @Test
    public void viewCountTest(){

        groupHashtagService.saveWord("테스트");

        String prefix = "테";

        Set<String> result = groupHashtagService.getAutoCompleteWord(prefix);

        assertTrue(result.contains("테스트"));
    }
}
