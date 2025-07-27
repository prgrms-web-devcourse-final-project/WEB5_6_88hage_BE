package com.grepp.funfun.app.domain.integrate;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.service.DataPipeline;
import com.grepp.funfun.app.domain.content.service.KakaoGeoService;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.vo.EventType;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@Transactional
class DataPipelineIntegrationTest {

    @Autowired
    private DataPipeline dataPipeline;

    @Autowired
    private KakaoGeoService kakaoGeoService;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EntityManager entityManager;

    private ContentCategory savedTheaterCategory;
    private ContentCategory savedMusicalCategory;

    @BeforeEach
    void setUp() {
        ContentCategory theaterCategory = new ContentCategory();
        theaterCategory.setCategory(ContentClassification.THEATER);
        savedTheaterCategory = entityManager.merge(theaterCategory);

        ContentCategory musicalCategory = new ContentCategory();
        musicalCategory.setCategory(ContentClassification.MUSICAL);
        savedMusicalCategory = entityManager.merge(musicalCategory);

        entityManager.flush();
    }

    @Test
    @DisplayName("DataPipeline - Content 처리 통합 테스트")
    void dataPipelineTest() {
        dataPipeline.importIncrementalData();

        List<Content> all = contentRepository.findAll();
        assertThat(all).isNotEmpty();

        log.info("처리된 컨텐츠 수: {}", all.size());

        all.forEach(content -> {
            assertThat(content.getContentTitle()).isNotNull();
            assertThat(content.getCategory()).isNotNull();
            log.info("처리된 컨텐츠: {}, 카테고리: {}",
                    content.getContentTitle(),
                    content.getCategory().getCategory());
        });
    }

    @Test
    @DisplayName("전체 데이터 수집 및 지오코딩 통합 테스트")
    void fullDataCollectionWithGeocoding() {
        // Given
        Content testContent1 = createTestContent("테스트 극장1", "서울특별시 예술의 전당", "서울특별시");
        Content testContent2 = createTestContent("테스트 극장2", "서울특별시 나루아트센터", "서울특별시");
        Content testContent3 = createTestContent("테스트 극장3", "경기도 거암아트홀", "서울특별시");

        List<Content> testContents = Arrays.asList(testContent1, testContent2, testContent3);
        contentRepository.saveAll(testContents);

        // When
        kakaoGeoService.updateContentCoordinates(testContents);

        // Then
        List<Content> remainingContents = contentRepository.findAll();

        log.info("========== 전체 데이터 수집 및 지오코딩 결과 ==========");
        log.info("처리 전 컨텐츠 수: {}", testContents.size());
        log.info("처리 후 컨텐츠 수: {}", remainingContents.size());

        for (Content content : remainingContents) {
            log.info("컨텐츠: {}, 주소: {}, 위도: {}, 경도: {}, 구: {}",
                    content.getContentTitle(), content.getAddress(),
                    content.getLatitude(), content.getLongitude(), content.getGuname());
        }

        for (Content content : remainingContents) {
            if (content.getArea().equals("서울특별시") && content.getAddress().contains("서울특별시")) {
                assertThat(content.getLatitude()).isNotNull();
                assertThat(content.getLongitude()).isNotNull();
            }
        }
    }

    @Test
    @DisplayName("증분 데이터 수집 시나리오 테스트")
    void incrementalDataCollection() {
        // Given
        Content existingContent = createTestContent("기존 극장", "서울특별시 예술의전당", "서울특별시");
        existingContent.setExternalId("EXISTING001");
        contentRepository.save(existingContent);

        // 새로운 데이터
        Content newContent = createTestContent("신규 극장", "서울특별시 예술의전당", "서울특별시");
        newContent.setExternalId("NEW001");

        List<Content> incrementalContents = Arrays.asList(newContent);

        // When - 증분 업데이트 수행
        contentRepository.saveAll(incrementalContents);
        kakaoGeoService.updateContentCoordinates(incrementalContents);

        // Then
        List<Content> allContents = contentRepository.findAll();
        assertThat(allContents.size()).isGreaterThanOrEqualTo(2);

        Optional<Content> existingFound = contentRepository.findByExternalId("EXISTING001");
        Optional<Content> newFound = contentRepository.findByExternalId("NEW001");

        assertThat(existingFound).isPresent();
        assertThat(newFound).isPresent();

        log.info("증분 데이터 수집 완료 - 기존: {}, 신규: {}",
                existingFound.get().getContentTitle(), newFound.get().getContentTitle());
    }

    @Test
    @DisplayName("카테고리별 Content 처리 테스트")
    void processByCategory() {
        // Given
        Content theaterContent = createTestContentWithCategory("연극", savedTheaterCategory);
        Content musicalContent = createTestContentWithCategory("뮤지컬", savedMusicalCategory);

        List<Content> contents = Arrays.asList(theaterContent, musicalContent);
        contentRepository.saveAll(contents);

        // When
        kakaoGeoService.updateContentCoordinates(contents);

        // Then
        List<Content> results = contentRepository.findAll();

        long theaterCount = results.stream()
                .filter(c -> c.getCategory().getCategory() == ContentClassification.THEATER)
                .count();
        long musicalCount = results.stream()
                .filter(c -> c.getCategory().getCategory() == ContentClassification.MUSICAL)
                .count();

        assertThat(theaterCount).isGreaterThanOrEqualTo(1);
        assertThat(musicalCount).isGreaterThanOrEqualTo(1);

        log.info("카테고리별 처리 결과 - 연극: {}개, 뮤지컬: {}개", theaterCount, musicalCount);
    }

    @Test
    @DisplayName("DataPipeline 에러 처리 테스트")
    void errorHandling() {
        // Given
        Content invalidAddressContent = createTestContent("잘못된 주소", "존재하지않는주소12345", "서울특별시");
        Content nullCategoryContent = new Content();
        nullCategoryContent.setContentTitle("카테고리 없음");
        nullCategoryContent.setAddress("서울특별시 중구 명동");
        nullCategoryContent.setArea("서울특별시");
        nullCategoryContent.setStartDate(LocalDate.now());
        nullCategoryContent.setEndDate(LocalDate.now().plusDays(30));
        nullCategoryContent.setEventType(EventType.EVENT);
        nullCategoryContent.setBookmarkCount(0);
        nullCategoryContent.setImages(Collections.emptyList());
        nullCategoryContent.setUrls(Collections.emptyList());

        // When & Then
        try {
            contentRepository.save(invalidAddressContent);
            kakaoGeoService.updateContentCoordinates(Arrays.asList(invalidAddressContent));
            log.info("잘못된 주소 컨텐츠 처리 완료");
        } catch (Exception e) {
            log.info("예상된 에러 발생: {}", e.getMessage());
        }

        try {
            contentRepository.save(nullCategoryContent);
        } catch (Exception e) {
            log.info("카테고리 없는 컨텐츠 저장 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("대용량 데이터 처리 성능 테스트")
    void performanceTest() {
        // Given
        List<Content> batchContents = Arrays.asList(
                createTestContent("성능테스트1", "서울특별시 중구 을지로 100", "서울특별시"),
                createTestContent("성능테스트2", "서울특별시 강남구 강남대로 200", "서울특별시"),
                createTestContent("성능테스트3", "서울특별시 서초구 서초대로 300", "서울특별시"),
                createTestContent("성능테스트4", "서울특별시 송파구 올림픽로 400", "서울특별시"),
                createTestContent("성능테스트5", "서울특별시 마포구 월드컵로 500", "서울특별시")
        );

        contentRepository.saveAll(batchContents);

        // When
        long startTime = System.currentTimeMillis();
        kakaoGeoService.updateContentCoordinates(batchContents);
        long endTime = System.currentTimeMillis();

        // Then
        long processingTime = endTime - startTime;
        log.info("========== 성능 테스트 결과 ==========");
        log.info("처리된 컨텐츠 수: {}", batchContents.size());
        log.info("총 처리 시간: {}ms", processingTime);
        log.info("평균 처리 시간: {}ms/건", processingTime / batchContents.size());

        assertThat(processingTime / batchContents.size()).isLessThan(5000);
    }

    private ContentDTO createMockContentDTO(String externalId, String title, String category) {
        return ContentDTO.builder()
                .externalId(externalId)
                .contentTitle(title)
                .category(category)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .address("서울특별시 중구 명동길 123")
                .area("서울특별시")
                .age("전체관람가")
                .fee("무료")
                .bookmarkCount(0)
                .images(Collections.emptyList())
                .urls(Collections.emptyList())
                .build();
    }

    private Content createTestContent(String title, String address, String area) {
        Content content = new Content();
        content.setContentTitle(title);
        content.setAddress(address);
        content.setArea(area);
        content.setCategory(savedTheaterCategory);
        content.setStartDate(LocalDate.now());
        content.setEndDate(LocalDate.now().plusDays(30));
        content.setEventType(EventType.EVENT);
        content.setBookmarkCount(0);
        content.setImages(Collections.emptyList());
        content.setUrls(Collections.emptyList());
        return content;
    }

    private Content createTestContentWithCategory(String title, ContentCategory category) {
        Content content = new Content();
        content.setContentTitle(title);
        content.setAddress("서울특별시 중구 명동길 123");
        content.setArea("서울특별시");
        content.setCategory(category);
        content.setStartDate(LocalDate.now());
        content.setEndDate(LocalDate.now().plusDays(30));
        content.setEventType(EventType.EVENT);
        content.setBookmarkCount(0);
        content.setImages(Collections.emptyList());
        content.setUrls(Collections.emptyList());
        return content;
    }
}