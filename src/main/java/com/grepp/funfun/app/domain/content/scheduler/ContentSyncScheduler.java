package com.grepp.funfun.app.domain.content.scheduler;

import com.grepp.funfun.app.domain.content.service.DataPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
// 스케줄링(현재 날짜로부터 등록/수정된 항목 업데이트)
public class ContentSyncScheduler {

    private final DataPipeline dataPipeline;

    @Scheduled(cron = "0 0 0 * * *")
    public void importContentFromOpenAPI() {
        try {
            dataPipeline.importIncrementalData();
        } catch (Exception e) {
            log.error("콘텐츠 수집 중 오류 발생", e);
        }
        log.info("[콘텐츠 수집 스케줄러 종료]");
    }
}
