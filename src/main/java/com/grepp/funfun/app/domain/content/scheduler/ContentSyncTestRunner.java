package com.grepp.funfun.app.domain.content.scheduler;

import com.grepp.funfun.app.domain.content.service.DataPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
@Slf4j
// 앱 실행 시 자동 실행
public class ContentSyncTestRunner implements CommandLineRunner {

    private final DataPipeline dataPipeline;

    @Override
    public void run(String... args) {
        log.info("[콘텐츠 수집 CommandLineRunner 테스트 시작]");
        try {
            dataPipeline.importFromOpenApi();
        } catch (Exception e) {
            log.error("[CommandLineRunner 수집 오류]", e);
        }
        log.info("[콘텐츠 수집 CommandLineRunner 테스트 종료]");
    }
}