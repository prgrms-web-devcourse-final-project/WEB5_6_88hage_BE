package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.content.vo.EventType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@Slf4j
class KakaoGeoServiceTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private ContentCategoryRepository contentCategoryRepository;

    @SpyBean
    private KakaoGeoService kakaoGeoService;

    @BeforeEach
    void setUp() {
        calendarRepository.deleteAll();
        contentRepository.deleteAll();
        contentCategoryRepository.deleteAll();
    }

    @Test
    void kakaoApiAddressTest() {
        ContentCategory category = new ContentCategory();
        category.setCategory(ContentClassification.POP_MUSIC);
        category = contentCategoryRepository.save(category);

        Content content = Content.builder()
                .address("예술의정당")
                .area("서울특별시")
                .category(category)
                .contentTitle("테스트 컨텐츠 제목")
                .eventType(EventType.EVENT)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .build();
        content = contentRepository.save(content);

        String fakeAddress = "서울특별시 종로구 세종대로 175";
        double fakeLat = 37.5720;
        double fakeLng = 126.9769;

        doReturn(Optional.of(new double[]{fakeLat, fakeLng}))
                .when(kakaoGeoService).getCoordinatesFromKeywordSearch(anyString());

        doReturn(Optional.of(fakeAddress))
                .when(kakaoGeoService).getAddressFromCoordinates(anyDouble(), anyDouble());

        kakaoGeoService.updateContentCoordinates(List.of(content));

        Content updated = contentRepository.findById(content.getId()).get();

        log.info("=============== 테스트 결과 ===============");
        log.info("원본 주소: {}", content.getAddress());
        log.info("업데이트된 주소: {}", updated.getAddress());
        log.info("위도: {}", updated.getLatitude());
        log.info("경도: {}", updated.getLongitude());
        log.info("구이름: {}", updated.getGuname());

        assertThat(updated.getLatitude()).isEqualTo(fakeLat);
        assertThat(updated.getLongitude()).isEqualTo(fakeLng);
        assertThat(updated.getAddress()).contains("세종대로");
        assertThat(updated.getGuname()).isEqualTo("종로구");
    }
}