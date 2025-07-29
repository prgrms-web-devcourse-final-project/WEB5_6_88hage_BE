package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.infra.kopis.KopisApiClient;
import com.grepp.funfun.app.infra.kopis.KopisXmlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPipeline {

    private final KopisApiClient kopisApiClient;
    private final KopisXmlParser kopisXmlParser;
    private final ContentPersistenceService contentPersistenceService;
    private final KakaoGeoService kakaoGeoService;

    private static final int MAX_API_CALLS = 1500;
    private int apiCallCount = 0;

    public void importFullData() {
        importFromOpenApi(false);
    }

    public void importIncrementalData() {
        importFromOpenApi(true);
    }

    private void importFromOpenApi(boolean incremental) {
        log.info("API로 컨텐츠 수집 시작 ({})", incremental ? "증분" : "전체");

        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String startDate = today;
            String endDate = LocalDate.now().plusMonths(6).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String afterDate = incremental ? "20250725" : null;

            List<String> dataTypes = List.of("pblprfr");
            List<Content> updatedOrInserted = new ArrayList<>();

            for (String type : dataTypes) {
                int page = 1;
                while (apiCallCount < MAX_API_CALLS) {
                    String idXml = kopisApiClient.fetchIdList(type, startDate, endDate, afterDate, page);
                    List<String> contentIds = kopisXmlParser.parseIdList(idXml);
                    if (contentIds.isEmpty()) break;

                    for (String id : contentIds) {
                        if (apiCallCount++ >= MAX_API_CALLS) break;
                        String detailXml = kopisApiClient.fetchDetail(id);
                        ContentDTO dto = kopisXmlParser.parseDetail(detailXml);
                        contentPersistenceService.saveOrUpdate(dto).ifPresent(saved -> {
                            updatedOrInserted.add(saved);
                            log.info("[저장 성공] ID: {}, 제목: {}", saved.getExternalId(), saved.getContentTitle());
                        });
                    }
                    page++;
                }
            }

            if (incremental) kakaoGeoService.updateContentCoordinates(updatedOrInserted);
            else kakaoGeoService.updateAllContentCoordinates();

            log.info("자동화 수집 완료: 총 {}개 저장", updatedOrInserted.size());
        } catch (Exception e) {
            log.error("자동화 수집 중 오류 발생", e);
        }
    }
}
