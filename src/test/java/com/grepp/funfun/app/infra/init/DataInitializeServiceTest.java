package com.grepp.funfun.app.infra.init;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataInitializeServiceTest {


    @Autowired
    DataInitializeService dataInitializeService;

    @Test
    public void initTest() {
        dataInitializeService.initializeVector();
    }
}