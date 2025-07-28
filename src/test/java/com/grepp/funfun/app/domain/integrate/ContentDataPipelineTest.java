package com.grepp.funfun.app.domain.integrate;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@SpringBootTest
@Slf4j
@Transactional
@Rollback
public class ContentDataPipelineTest {
    @Autowired
    private DataPipeline dataPipeline;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private KakaoGeoService kakaoGeoService;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private EntityManager entityManager;

    private ContentCategory savedTheaterCategory;

    @BeforeEach
    void setUp() {
        calendarRepository.deleteAll();
        ContentCategory theaterCategory = new ContentCategory();
        theaterCategory.setCategory(ContentClassification.THEATER);
        savedTheaterCategory = entityManager.merge(theaterCategory);


        entityManager.flush();
    }

    @Test
    @DisplayName("DataPipeline - Content 증분 데이터 처리 통합 테스트")
    void dataPipelineTest() {
        dataPipeline.importIncrementalData();

        List<Content> all = contentRepository.findAll();

        log.info("저장된 콘텐츠 개수: {}", all.size());
        all.forEach(content ->
                log.info("[{}] {} ~ {}", content.getContentTitle(), content.getStartDate(), content.getEndDate())
        );
    }

    @Test
    @DisplayName("DataPipeline - Content 모든 데이터 처리 통합 테스트")
    void dataPipelineAllTest() {
        dataPipeline.importFullData();

        List<Content> all = contentRepository.findAll();

        log.info("저장된 콘텐츠 개수: {}", all.size());
        all.forEach(content ->
                log.info("[{}] {} ~ {}", content.getContentTitle(), content.getStartDate(), content.getEndDate())
        );
    }

    @Test
    @DisplayName("updateContent 메서드 직접 테스트")
    void updateContent_DirectTest() {
        // Given
        Content existingContent = createTestContent("원본 제목", "예술의전당", "서울특별시");
        existingContent.setExternalId("DIRECT001");
        Content saved = contentRepository.save(existingContent);

        ContentDTO updateDTO = ContentDTO.builder()
                .externalId("DIRECT001")
                .contentTitle("변경된 제목")
                .age("변경된 연령")
                .fee("변경된 요금")
                .address("거암아트홀")
                .area("서울특별시")
                .startDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusDays(31))
                .images(Collections.emptyList())
                .urls(Collections.emptyList())
                .build();

        // When
        try {
            Method updateMethod = DataPipeline.class.getDeclaredMethod("updateContent", Content.class, ContentDTO.class);
            updateMethod.setAccessible(true);
            updateMethod.invoke(dataPipeline, saved, updateDTO);

            Content updatedContent = contentRepository.save(saved);

            // Then
            assertThat(updatedContent.getContentTitle()).isEqualTo("변경된 제목");
            assertThat(updatedContent.getAge()).isEqualTo("변경된 연령");
            assertThat(updatedContent.getFee()).isEqualTo("변경된 요금");
            assertThat(updatedContent.getAddress()).isEqualTo("거암아트홀");

            log.info("테스트 성공");

        } catch (Exception e) {
            fail("updateContent 메서드 호출 실패: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("전체 데이터 수집 및 지오코딩 테스트")
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
    @DisplayName("증분 데이터 -> 위치 테스트")
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

}
