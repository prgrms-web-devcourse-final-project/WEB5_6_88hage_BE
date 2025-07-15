package com.grepp.funfun.app.infra.init;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class DataInitializeServiceTest {


    @Autowired
    DataInitializeService dataInitializeService;

    @Autowired
    ContentRepository contentRepository;

    @Test
    public void initTest() {
        dataInitializeService.initializeVector();
    }


    @Test
    @Transactional
    public void search() {
        List<Content> contents = contentRepository.findAll();

        log.info("테스트 결과 {}",contents.getFirst().getCategory().getCategory());

    }


}