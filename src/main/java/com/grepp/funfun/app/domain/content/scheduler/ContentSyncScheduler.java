package com.grepp.funfun.app.domain.content.scheduler;

import com.grepp.funfun.app.domain.content.service.DataPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
// 스케줄링만 담당
public class ContentSyncScheduler {

    private final DataPipeline dataPipeline;

    @Scheduled(cron = "0 0 0 * * *")
    public void importContentFromOpenAPI() {
        try {
            dataPipeline.importFromOpenApi();
        } catch (Exception e) {
            log.error("콘텐츠 수집 중 오류 발생", e);
        }
    }
}
