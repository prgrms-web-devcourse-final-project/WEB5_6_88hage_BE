package com.grepp.funfun.app.domain.content.scheduler;

import com.grepp.funfun.app.domain.content.service.ContentSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentSyncScheduler {

    private final ContentSyncService contentSyncService;

//    @Scheduled(cron = "0 0 4 * * *")
//    @Scheduled(fixedDelay = 60000)
//    public void syncContents() {
//        log.info("외부 콘텐츠 동기화 작업 시작");
//        contentSyncService.fetchAndSaveContents();
//        log.info("외부 콘텐츠 동기화 작업 완료");
//    }
}
