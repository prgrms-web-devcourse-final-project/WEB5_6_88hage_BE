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
import java.time.format.DateTimeFormatter;
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
        String contentId = "CONTENT123";

        ContentDTO dto = ContentDTO.builder()
                .externalId("EXT123")
                .contentTitle("Dummy")
                .category("POP_MUSIC")
                .eventType(EventType.EVENT)
                .urls(List.of())
                .images(List.of())
                .latitude(37.0)
                .longitude(127.0)
                .build();

        ContentCategory dummyCategory = ContentCategory.builder()
                .category(ContentClassification.POP_MUSIC)
                .build();

        when(contentCategoryRepository.findByCategory(ContentClassification.POP_MUSIC))
                .thenReturn(Optional.of(dummyCategory));

        DataPipeline testPipeline = new TestableDataPipeline(
                List.of(contentId),
                dto,
                contentRepository,
                contentCategoryRepository,
                kakaoGeoService
        );

        ReflectionTestUtils.setField(testPipeline, "kopisApiKey", "dummy-key");

        // when
        testPipeline.importIncrementalData();

        // then
        verify(kakaoGeoService).updateContentCoordinates(argThat(list ->
                list.size() == 1 && list.get(0).getContentTitle().equals("Dummy")
        ));
    }

    @Test
    @DisplayName("전체 수집 시 - updateAllContentCoordinates() 호출됨")
    void testImportFullData_callsUpdateAllCoordinates() {
        // given
        String contentId = "CONTENT123";

        ContentDTO dto = ContentDTO.builder()
                .externalId("EXT999")
                .contentTitle("FullContent")
                .category("POP_MUSIC")
                .eventType(EventType.EVENT)
                .urls(List.of())
                .images(List.of())
                .latitude(37.5)
                .longitude(126.9)
                .build();

        ContentCategory dummyCategory = ContentCategory.builder()
                .category(ContentClassification.POP_MUSIC)
                .build();

        when(contentRepository.findByExternalId("EXT999")).thenReturn(Optional.empty());
        when(contentCategoryRepository.findByCategory(ContentClassification.POP_MUSIC))
                .thenReturn(Optional.of(dummyCategory));

        DataPipeline testPipeline = new TestableDataPipeline(
                List.of(contentId),
                dto,
                contentRepository,
                contentCategoryRepository,
                kakaoGeoService
        );

        ReflectionTestUtils.setField(testPipeline, "kopisApiKey", "dummy-key");

        // when
        testPipeline.importFullData();

        // then
        verify(kakaoGeoService, times(1)).updateAllContentCoordinates();
        verify(kakaoGeoService, never()).updateContentCoordinates(any());
    }

    @Test
    @DisplayName("증분 수집 시 afterDate 기준으로 기존 콘텐츠가 업데이트됨")
    void testImportIncrementalData_updatesExistingContent() {
        // given
        String contentId = "CID123";

        ContentDTO dto = ContentDTO.builder()
                .externalId("EXT123")
                .contentTitle("New Title")
                .address(null)
                .age("15세 이상")
                .latitude(37.1)
                .longitude(127.1)
                .urls(List.of(ContentUrlDTO.builder()
                        .siteName("예스24").url("http://yes24.com").build()))
                .images(List.of(ContentImageDTO.builder()
                        .imageUrl("image.jpg").build()))
                .category("POP_MUSIC")
                .eventType(EventType.EVENT)
                .build();

        ContentCategory category = ContentCategory.builder()
                .category(ContentClassification.POP_MUSIC)
                .build();

        Content existing = Content.builder()
                .id(999L)
                .externalId("EXT123")
                .contentTitle("Old Title")
                .address("서울특별시 예술의전당")
                .images(new ArrayList<>())
                .urls(new ArrayList<>())
                .category(category)
                .eventType(EventType.EVENT)
                .build();

        when(contentRepository.findByExternalId("EXT123")).thenReturn(Optional.of(existing));

        TestableDataPipeline pipeline = new TestableDataPipeline(
                List.of(contentId), dto, contentRepository, contentCategoryRepository, kakaoGeoService
        );

        // when
        pipeline.importIncrementalData();

        // then
        ArgumentCaptor<Content> captor = ArgumentCaptor.forClass(Content.class);
        verify(contentRepository).save(captor.capture());

        Content saved = captor.getValue();
        assertEquals("New Title", saved.getContentTitle());
        assertEquals(1, saved.getImages().size());
        assertEquals("image.jpg", saved.getImages().get(0).getImageUrl());
        assertEquals("예스24", saved.getUrls().get(0).getSiteName());

        verify(kakaoGeoService).updateContentCoordinates(any());
        verify(kakaoGeoService, never()).updateAllContentCoordinates();
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



    static class TestableDataPipeline extends DataPipeline {
        private final List<String> idsToReturn;
        private final ContentDTO dtoToReturn;

        public TestableDataPipeline(
                List<String> ids,
                ContentDTO dtoToReturn,
                ContentRepository contentRepo,
                ContentCategoryRepository categoryRepo,
                KakaoGeoService geoService
        ) {
            super(contentRepo, geoService, categoryRepo);
            this.idsToReturn = ids;
            this.dtoToReturn = dtoToReturn;
        }

        @Override
        protected List<String> getIdList(String a, String b, String c, String d) {
            return idsToReturn;
        }

        @Override
        protected ContentDTO getDetailInfo(String id) {
            return dtoToReturn;
        }
    }

}