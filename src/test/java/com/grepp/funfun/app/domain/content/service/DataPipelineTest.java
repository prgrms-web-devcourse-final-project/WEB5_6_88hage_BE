package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import com.grepp.funfun.app.domain.content.dto.ContentUrlDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.vo.EventType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class DataPipelineTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ContentCategoryRepository contentCategoryRepository;

    @Mock
    private KakaoGeoService kakaoGeoService;

    @InjectMocks
    private DataPipeline dataPipeline;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dataPipeline, "kopisApiKey", "dummy-key");
    }

    @Test
    @DisplayName("증분 수집 시 - updateContentCoordinates() 호출됨")
    void testImportIncrementalData_updatesOnlyUpdatedContents() {
        // given
        Content dummyContent = Content.builder().id(1L).contentTitle("Dummy").build();

        // ID 목록 반환
        Mockito.doReturn(List.of("CONTENT123"))
                .when(dataPipeline)
                .getIdList(anyString(), anyString(), anyString(), anyString());

        // 상세 데이터 처리 결과 (추가 또는 수정된 콘텐츠 반환)
        Mockito.doReturn(Optional.of(dummyContent))
                .when(dataPipeline)
                .processContent(anyString());

        // when
        dataPipeline.importIncrementalData();

        // then
        verify(kakaoGeoService, times(1)).updateContentCoordinates(argThat(list ->
                list.size() == 1 && list.get(0).getContentTitle().equals("Dummy")));
        verify(kakaoGeoService, never()).updateAllContentCoordinates();
    }

    @Test
    @DisplayName("전체 수집 시 - updateAllContentCoordinates() 호출됨")
    void testImportFullData_callsUpdateAllCoordinates() {
        // given
        Mockito.doReturn(List.of("CONTENT123"))
                .when(dataPipeline)
                .getIdList(anyString(), anyString(), anyString(), isNull());

        Mockito.doReturn(Optional.of(Content.builder().id(1L).contentTitle("FullContent").build()))
                .when(dataPipeline)
                .processContent(anyString());

        // when
        dataPipeline.importFullData();

        // then
        verify(kakaoGeoService, times(1)).updateAllContentCoordinates();
        verify(kakaoGeoService, never()).updateContentCoordinates(any());
    }

    @Test
    @DisplayName("기존 콘텐츠가 있을 경우 updateContent 후 저장된다")
    void testProcessContent_updatesExisting() {
        ContentCategory dummyCategory = ContentCategory.builder()
                .category(ContentClassification.POP_MUSIC)
                .build();

        String contentId = "CID001";

        ContentDTO dto = ContentDTO.builder()
                .externalId("EXT001")
                .contentTitle("Updated Title")
                .age("만 15세 이상")
                .latitude(37.123)
                .longitude(127.456)
                .images(List.of(ContentImageDTO.builder().imageUrl("img.jpg").build()))
                .urls(List.of(ContentUrlDTO.builder().siteName("예스24").url("http://yes24.com").build()))
                .eventType(EventType.EVENT)
                .build();

        Content existing = Content.builder()
                .id(10L)
                .externalId("EXT001")
                .contentTitle("Old Title")
                .images(new ArrayList<>())
                .urls(new ArrayList<>())
                .category(dummyCategory)
                .eventType(EventType.EVENT)
                .build();

        when(contentRepository.findByExternalId("EXT001")).thenReturn(Optional.of(existing));

        DataPipeline spyPipeline = Mockito.spy(dataPipeline);
        doReturn(dto).when(spyPipeline).getDetailInfo(contentId);

        Optional<Content> result = ReflectionTestUtils.invokeMethod(spyPipeline, "processContent", contentId);

        assertTrue(result.isPresent());

        ArgumentCaptor<Content> captor = ArgumentCaptor.forClass(Content.class);
        verify(contentRepository).save(captor.capture());

        Content updated = result.get();
        assertEquals("Updated Title", updated.getContentTitle());
        assertEquals(1, updated.getImages().size());
        assertEquals("img.jpg", updated.getImages().get(0).getImageUrl());
        assertEquals("예스24", updated.getUrls().get(0).getSiteName());

        log.info("엡데이트된 콘텐츠: {}", updated);

    }


    @Test
    @DisplayName("신규 콘텐츠일 경우 저장되고, 리스트에 추가된다")
    void testSaveNewContent_savesNewEntity() {
        String contentId = "NEW123";

        ContentDTO dto = ContentDTO.builder()
                .externalId("NEW123")
                .contentTitle("New Performance")
                .category("POP_MUSIC")
                .age("전체관람가")
                .fee("무료")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2025, 9, 2))
                .runTime("100분")
                .time("15:00")
                .startTime("15:00")
                .address("서울시 종로구")
                .area("서울특별시")
                .guname("종로구")
                .latitude(37.123)
                .longitude(127.456)
                .eventType(EventType.EVENT)
                .images(List.of(ContentImageDTO.builder().imageUrl("img1.jpg").build()))
                .urls(List.of(ContentUrlDTO.builder().siteName("예스24").url("http://yes24.com").build()))
                .build();

        when(contentRepository.findByExternalId("NEW123")).thenReturn(Optional.empty());

        when(contentCategoryRepository.findByCategory(ContentClassification.POP_MUSIC))
                .thenReturn(Optional.of(com.grepp.funfun.app.domain.content.entity.ContentCategory.builder()
                        .category(ContentClassification.POP_MUSIC)
                        .build()));

        DataPipeline spyPipeline = Mockito.spy(dataPipeline);
        doReturn(dto).when(spyPipeline).getDetailInfo(contentId);

        Optional<Content> result = ReflectionTestUtils.invokeMethod(spyPipeline, "processContent", contentId);

        assertTrue(result.isPresent());

        ArgumentCaptor<Content> captor = ArgumentCaptor.forClass(Content.class);
        verify(contentRepository).save(captor.capture());


        Content saved = result.get();
        assertEquals("New Performance", saved.getContentTitle());
        assertEquals("서울시 종로구", saved.getAddress());
        assertEquals(1, saved.getImages().size());
        assertEquals("img1.jpg", saved.getImages().get(0).getImageUrl());
        assertEquals("예스24", saved.getUrls().get(0).getSiteName());

        log.info("[신규 저장] 저장된 콘텐츠: {}", saved);

    }


}