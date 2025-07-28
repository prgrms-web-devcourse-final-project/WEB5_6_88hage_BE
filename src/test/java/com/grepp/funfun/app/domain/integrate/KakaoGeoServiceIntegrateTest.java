package com.grepp.funfun.app.domain.integrate;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.service.KakaoGeoService;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.vo.EventType;
import com.grepp.funfun.app.infra.kakao.KakaoApiClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@Slf4j
class KakaoGeoServiceIntegrateTest {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentCategoryRepository contentCategoryRepository;

    @Autowired
    private KakaoGeoService kakaoGeoService;

    @MockBean
    private KakaoApiClient kakaoApiClient;

    @Test
    @DisplayName("[통합] 위경도 및 주소가 실제 DB에 반영되는지 확인")
    void updateContentCoordinates_integration() {
        ContentCategory category = new ContentCategory();
        category.setCategory(ContentClassification.THEATER);
        category = contentCategoryRepository.save(category);

        Content content = new Content();
        content.setContentTitle("통합테스트용 공연");
        content.setAddress("서울특별시 마포구 상암동");
        content.setArea("서울특별시");
        content.setEventType(EventType.EVENT);
        content.setCategory(category);
        content = contentRepository.save(content);

        log.info("저장 전 content: {}", content);

        when(kakaoApiClient.getCoordinatesFromKeywordSearch(anyString()))
                .thenReturn(Optional.of(new double[]{37.566, 126.978}));
        when(kakaoApiClient.getAddressFromCoordinates(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("서울특별시 마포구 월드컵북로 400"));

        kakaoGeoService.updateContentCoordinates(List.of(content));

        Content updated = contentRepository.findById(content.getId()).orElseThrow();
        log.info("업데이트된 content: {}", updated);
        assertThat(updated.getLatitude()).isEqualTo(37.566);
        assertThat(updated.getLongitude()).isEqualTo(126.978);
        assertThat(updated.getGuname()).isEqualTo("마포구");
    }
}