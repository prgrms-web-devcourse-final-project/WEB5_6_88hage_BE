package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.infra.kakao.AddressPreprocessor;
import com.grepp.funfun.app.infra.kakao.KakaoApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class KakaoGeoServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private AddressPreprocessor addressPreprocessor;

    @Mock
    private KakaoApiClient kakaoApiClient;

    @InjectMocks
    private KakaoGeoService kakaoGeoService;

    private Content testContent;

    @BeforeEach
    void setUp() {
        ContentCategory category = new ContentCategory();
        category.setCategory(ContentClassification.THEATER);

        testContent = new Content();
        testContent.setId(1L);
        testContent.setContentTitle("테스트 공연");
        testContent.setAddress("서울특별시 강남구 역삼동");
        testContent.setArea("서울특별시");
        testContent.setCategory(category);
    }

    @Test
    @DisplayName("KakaoGeoService - 위경도 및 주소 업데이트 성공")
    void updateContentCoordinates_success() {
        List<Content> contents = List.of(testContent);

        double latitude = 37.501;
        double longitude = 127.037;
        when(kakaoApiClient.getCoordinatesFromKeywordSearch(anyString()))
                .thenReturn(Optional.of(new double[]{latitude, longitude}));


        when(kakaoApiClient.getAddressFromCoordinates(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("서울특별시 강남구 테헤란로 123"));

        when(addressPreprocessor.preprocessAddress(anyString()))
                .thenReturn("서울특별시 강남구 테헤란로 123");

        when(addressPreprocessor.extractGunameFromAddress(anyString()))
                .thenReturn("강남구");

        kakaoGeoService.updateContentCoordinates(contents);

        assertThat(testContent.getLatitude()).isEqualTo(37.501);
        assertThat(testContent.getLongitude()).isEqualTo(127.037);
        assertThat(testContent.getAddress()).contains("테헤란로");
        assertThat(testContent.getGuname()).isEqualTo("강남구");

        verify(contentRepository, times(1)).save(testContent);
    }

    @Test
    @DisplayName("KakaoGeoService - 위경도 실패시 저장하지 않음")
    void updateContentCoordinates_coordinateFail() {
        List<Content> contents = List.of(testContent);

        when(addressPreprocessor.preprocessAddress(anyString()))
                .thenReturn("서울특별시 강남구 역삼동");

        when(kakaoApiClient.getCoordinatesFromKeywordSearch(anyString()))
                .thenReturn(Optional.empty());

        kakaoGeoService.updateContentCoordinates(contents);

        verify(contentRepository).delete(any());
        verify(contentRepository, never()).save(any());
    }
}
