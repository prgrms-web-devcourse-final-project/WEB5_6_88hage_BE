package com.grepp.funfun.app.domain.integrate;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.service.KakaoGeoService;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.vo.EventType;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Slf4j
@Transactional
class KakaoGeoServiceTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private KakaoGeoService kakaoGeoService;

    @Autowired
    private EntityManager entityManager;

    private Content testContent;

    @BeforeEach
    void setUp() {
        ContentCategory category = new ContentCategory();
        category.setCategory(ContentClassification.THEATER);
        ContentCategory savedCategory = entityManager.merge(category);
        entityManager.flush();

        testContent = new Content();
        testContent.setContentTitle("국립극장 테스트");
        testContent.setAddress("서울특별시 중구 장충단로 59 국립극장");
        testContent.setArea("서울특별시");
        testContent.setGuname(null);
        testContent.setLatitude(null);
        testContent.setLongitude(null);
        testContent.setStartDate(LocalDate.now());
        testContent.setEndDate(LocalDate.now().plusDays(30));
        testContent.setCategory(savedCategory);
        testContent.setImages(Collections.emptyList());
        testContent.setUrls(Collections.emptyList());
        testContent.setEventType(EventType.EVENT);
    }

    @Test
    @DisplayName("키워드 검색으로 위경도 조회 - 성공 케이스")
    void getCoordinatesFromKeywordSearch_Success() {
        // Given
        String keyword = "서울특별시 중구 국립극장";

        // When
        Optional<double[]> result = kakaoGeoService.getCoordinatesFromKeywordSearch(keyword);

        // Then
        assertThat(result).isPresent();
        double[] coordinates = result.get();
        assertThat(coordinates).hasSize(2);

        double latitude = coordinates[0];
        double longitude = coordinates[1];

        assertThat(latitude).isBetween(37.0, 38.0);
        assertThat(longitude).isBetween(126.0, 128.0);

        log.info("키워드 '{}' 검색 결과: 위도={}, 경도={}", keyword, latitude, longitude);
    }

    @Test
    @DisplayName("키워드 검색으로 위경도 조회 - 존재하지 않는 장소")
    void getCoordinatesFromKeywordSearch_NotFound() {
        // Given
        String keyword = "존재하지않는장소12345";

        // When
        Optional<double[]> result = kakaoGeoService.getCoordinatesFromKeywordSearch(keyword);

        // Then
        assertThat(result).isEmpty();
        log.info("존재하지 않는 키워드 '{}' 검색 결과: 빈 값", keyword);
    }

    @Test
    @DisplayName("위경도로 주소 조회 - 성공 케이스")
    void getAddressFromCoordinates_Success() {
        // Given - 국립극장 위경도 (대략)
        double latitude = 37.5587;
        double longitude = 126.9939;

        // When
        Optional<String> result = kakaoGeoService.getAddressFromCoordinates(latitude, longitude);

        // Then
        assertThat(result).isPresent();
        String address = result.get();
        assertThat(address).isNotBlank();
        assertThat(address).contains("서울");

        log.info("위경도 ({}, {}) 역지오코딩 결과: {}", latitude, longitude, address);
    }

    @Test
    @DisplayName("위경도로 주소 조회 - 잘못된 좌표")
    void getAddressFromCoordinates_InvalidCoordinates() {
        // Given - 유효하지 않은 좌표
        double latitude = 999.0;
        double longitude = 999.0;

        // When
        Optional<String> result = kakaoGeoService.getAddressFromCoordinates(latitude, longitude);

        // Then
        assertThat(result).isEmpty();
        log.info("잘못된 좌표 ({}, {}) 역지오코딩 결과: 빈 값", latitude, longitude);
    }

    @Test
    @DisplayName("개별 컨텐츠 위경도 업데이트 테스트")
    void updateContentCoordinates_SingleContent() {
        // Given
        contentRepository.save(testContent);
        List<Content> contents = Arrays.asList(testContent);

        // When
        kakaoGeoService.updateContentCoordinates(contents);

        // Then
        Content updatedContent = contentRepository.findById(testContent.getId()).orElseThrow();

        assertThat(updatedContent.getLatitude()).isNotNull();
        assertThat(updatedContent.getLongitude()).isNotNull();
        assertThat(updatedContent.getLatitude()).isBetween(37.0, 38.0);
        assertThat(updatedContent.getLongitude()).isBetween(126.0, 128.0);

        // 구 정보가 설정되었는지 확인
        assertThat(updatedContent.getGuname()).isNotNull();
        assertThat(updatedContent.getGuname()).endsWith("구");

        log.info("업데이트된 컨텐츠: ID={}, 주소={}, 위도={}, 경도={}, 구={}",
                updatedContent.getId(), updatedContent.getAddress(),
                updatedContent.getLatitude(), updatedContent.getLongitude(), updatedContent.getGuname());
    }

    @Test
    @DisplayName("주소 전처리 테스트 - 괄호 제거")
    void preprocessAddress_RemoveBrackets() {
        // Given
        Content contents = new Content();
        contents.setContentTitle("테스트 공연장");
        contents.setAddress("서울특별시 강남구 (구)청사 앞 문화센터");
        contents.setArea("서울특별시");
        contents.setCategory(testContent.getCategory());
        contents.setStartDate(LocalDate.now());
        contents.setEndDate(LocalDate.now().plusDays(30));
        contents.setImages(Collections.emptyList());
        contents.setUrls(Collections.emptyList());
        contents.setEventType(EventType.EVENT);

        contentRepository.save(contents);

        // When
        kakaoGeoService.updateContentCoordinates(Arrays.asList(contents));

        // Then
        Content updatedContent = contentRepository.findById(contents.getId()).orElseThrow();

        assertThat(updatedContent.getAddress()).doesNotContain("(구)");

        log.info("괄호 제거 테스트 결과: {}", updatedContent.getAddress());
    }

    @Test
    @DisplayName("시/도 불일치 시 삭제 로직 테스트")
    void deleteContentWhenAreaMismatch() {
        Content mismatchContent = new Content();
        mismatchContent.setContentTitle("지역 불일치 테스트");
        mismatchContent.setAddress("경기도 성남시 분당구 정자동 카카오");
        mismatchContent.setArea("서울특별시");
        mismatchContent.setCategory(testContent.getCategory());
        mismatchContent.setStartDate(LocalDate.now());
        mismatchContent.setEndDate(LocalDate.now().plusDays(30));
        mismatchContent.setImages(Collections.emptyList());
        mismatchContent.setUrls(Collections.emptyList());
        mismatchContent.setEventType(EventType.EVENT);
        mismatchContent.setBookmarkCount(0);

        contentRepository.save(mismatchContent);
        Long contentId = mismatchContent.getId();

        // When
        kakaoGeoService.updateContentCoordinates(Arrays.asList(mismatchContent));

        // Then
        Optional<Content> result = contentRepository.findById(contentId);
        assertThat(result).isEmpty();

        log.info("지역 불일치로 인한 컨텐츠 삭제 확인: ID={}", contentId);
    }
}