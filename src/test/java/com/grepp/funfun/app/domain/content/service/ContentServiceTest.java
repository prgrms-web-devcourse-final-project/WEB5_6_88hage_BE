package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.ContentDetailDTO;
import com.grepp.funfun.app.domain.content.dto.ContentListDTO;
import com.grepp.funfun.app.domain.content.dto.ContentSimpleDTO;
import com.grepp.funfun.app.domain.content.dto.payload.ContentFilterRequest;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.user.dto.UserDTO;
import com.grepp.funfun.app.domain.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ContentServiceTest {

    @InjectMocks
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserService userService;

    private UserDTO mockUser;
    private List<Content> mockContents;

    @BeforeEach
    void injectRealModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.getConfiguration().setPreferNestedProperties(false);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        Converter<Content, String> categoryConverter = ctx -> {
            if (ctx.getSource() == null ||
                    ctx.getSource().getCategory() == null ||
                    ctx.getSource().getCategory().getCategory() == null) {
                return null;
            }
            return ctx.getSource().getCategory().getCategory().name();
        };

        modelMapper.typeMap(Content.class, ContentListDTO.class)
                .addMappings(mapper -> mapper.using(categoryConverter)
                        .map(src -> src, ContentListDTO::setCategory));

        modelMapper.typeMap(Content.class, ContentDetailDTO.class)
                .addMappings(mapper -> mapper.using(categoryConverter)
                        .map(src -> src, ContentDetailDTO::setCategory));

        modelMapper.typeMap(Content.class, ContentSimpleDTO.class)
                .addMappings(mapper -> mapper.using(categoryConverter)
                        .map(src -> src, ContentSimpleDTO::setCategory));

        contentService = new ContentService(contentRepository, userService, modelMapper);
        mockContents = createMockContents();
    }


    private void setupDistanceSortMocks() {
        mockUser = new UserDTO();
        mockUser.setEmail("test@example.com");
        mockUser.setLatitude(37.4981);
        mockUser.setLongitude(127.0276);

        given(userService.get("test@example.com")).willReturn(mockUser);
    }

    private void setupNoLocationMock() {
        mockUser = new UserDTO();
        mockUser.setEmail("test@example.com");
        mockUser.setLatitude(null);
        mockUser.setLongitude(null);
        given(userService.get("test@example.com")).willReturn(mockUser);
    }

    @Test
    @DisplayName("전체 조회 테스트 (기본 정렬: 가까운순)")
    public void getContents(){

        setupDistanceSortMocks();

        ContentFilterRequest request = new ContentFilterRequest();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Content> mockPage = new PageImpl<>(mockContents, pageable, mockContents.size());
        given(contentRepository.findFilteredContentsByDistance(
                any(), any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(false), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);

        log.info("========== 전체 조회 (기본 정렬: 가까운순) ===========");
        log.info("총 개수: {}", result.getTotalElements());
        log.info("정렬 방식: distance (기본값)");

        result.getContent().forEach(content ->
                log.info("{} - {} (위치: {}, {})", content.getId(), content.getContentTitle(), content.getLatitude(), content.getLongitude())
        );

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(false), eq(pageable));
    }

    @Test
    @DisplayName("무용 카테고리 필터링 테스트")
    public void getFilterByCategory(){
        setupDistanceSortMocks();

        ContentFilterRequest request = new ContentFilterRequest();
        request.setCategory(ContentClassification.DANCE);
        request.setKeyword(null);
        request.setSortBy("distance");

        Pageable pageable = PageRequest.of(0, 10);

        List<Content> danceContents = mockContents.stream()
                .filter(content -> content.getCategory().getCategory() == ContentClassification.DANCE)
                .toList();
        Page<Content> mockPage = new PageImpl<>(danceContents, pageable, danceContents.size());

        given(contentRepository.findFilteredContentsByDistance(
                eq(ContentClassification.DANCE), any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(false), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        result.getContent().forEach(dto ->
                assertThat(dto.getCategory()).isEqualTo("DANCE")
        );
    }

    @Test
    @DisplayName("강남구 지역 필터링 테스트")
    public void getFilterByGuname() {
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest();
        request.setGuname("강남구");
        Pageable pageable = PageRequest.of(0, 10);
        List<Content> gangnamContents = mockContents.stream()
                .filter(content -> "강남구".equals(content.getGuname()))
                .toList();
        Page<Content> mockPage = new PageImpl<>(gangnamContents, pageable, gangnamContents.size());

        given(contentRepository.findFilteredContentsByDistance(
                any(), eq("강남구"), any(), any(), any(), eq(37.4981), eq(127.0276), eq(false), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();
        if (!result.getContent().isEmpty()) {
            assertThat(result.getContent()).allMatch(content ->
                    "강남구".equals(content.getGuname())
            );

            log.info("======= 강남구 지역 필터링 =======");
            result.getContent().forEach(content ->
                    log.info("{} - 지역: {}", content.getContentTitle(), content.getGuname())
            );
        } else {
            log.info("강남구 컨텐츠가 없습니다.");
        }
    }

    @Test
    @DisplayName("1. 가까운순 정렬 테스트 (사용자 기본 위치 기준)")
    void testDistanceSortWithUserLocation() {
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest();
        request.setSortBy("distance");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Content> mockPage = new PageImpl<>(mockContents, pageable, mockContents.size());

        given(contentRepository.findFilteredContentsByDistance(
                any(), any(), any(), any(), any(),eq(37.4981), eq(127.0276),  eq(false),eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        log.info("========== 가까운순 정렬 (사용자 위치 기준) ==========");
        log.info("기준 위치: 강남역 (37.4981, 127.0276)");
        log.info("총 개수: {}", result.getTotalElements());

        result.getContent().forEach(content -> {
            Double lat = content.getLatitude();
            Double lng = content.getLongitude();
            String location = (lat != null && lng != null) ?
                    String.format("(%.4f, %.4f)", lat, lng) : "위치정보없음";
            log.info(content.getContentTitle() + " - " + location);
        });

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), any(), eq(37.4981), eq(127.0276),  eq(false),eq(pageable));
    }

    @Test
    @DisplayName("2. 북마크순 정렬 테스트")
    void testBookmarkCountDescSort() {
        String testEmail = "anonymousUser";
        // given
        ContentFilterRequest request = new ContentFilterRequest();
        request.setSortBy("bookmarkCount");
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "bookmarkCount"));

        Page<Content> mockPage = new PageImpl<>(mockContents, pageable, mockContents.size());

        given(contentRepository.findFilteredContents(
                any(), any(), any(), any(), any(), eq(false),
                argThat(p -> p.getSort().equals(Sort.by(Sort.Direction.DESC, "bookmarkCount")))))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(testEmail, request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        log.info("========== 북마크순 정렬 테스트 (Service 로직 검증) ==========");
        log.info("Service가 Repository에 전달한 정렬 조건 확인");
        log.info("총 개수: {}", result.getTotalElements());
        result.getContent().forEach(content ->
                log.info("{} - 북마크 {}",content.getContentTitle(),  content.getBookmarkCount())
        );

        verify(contentRepository).findFilteredContents(
                any(), any(), any(), any(), any(), eq(false),
                argThat(p -> {
                    Sort sort = p.getSort();
                    boolean isCorrectSort = sort.isSorted() &&
                            sort.getOrderFor("bookmarkCount") != null &&
                            sort.getOrderFor("bookmarkCount").getDirection() == Sort.Direction.DESC;

                    log.info("Service가 올바른 정렬 조건을 전달했는지: {}", isCorrectSort);
                    return isCorrectSort;
                }));
    }

    @Test
    @DisplayName("3. 마감 임박순 정렬 테스트 (가까운 날짜 순)")
    void testEndDateAscSort() {
        String testEmail = "anonymousUser";
        ContentFilterRequest request = new ContentFilterRequest();
        request.setSortBy("endDate");

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "endDate"));

        List<Content> sortedByEndDate = mockContents.stream()
                .sorted(Comparator.comparing(Content::getEndDate))
                .toList();
        Page<Content> mockPage = new PageImpl<>(sortedByEndDate, pageable, sortedByEndDate.size());

        given(contentRepository.findFilteredContents(any(), any(), any(), any(), any(), eq(false), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(testEmail, request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        for (int i = 0; i < result.getContent().size() - 1; i++) {
            LocalDate current = result.getContent().get(i).getEndDate();
            LocalDate next = result.getContent().get(i + 1).getEndDate();
            assertThat(current).isBeforeOrEqualTo(next);
        }

        log.info("========== 마감 임박순 정렬 (가까운 날짜 순) ==========");
        log.info("총 개수: {}", result.getTotalElements());
        result.getContent().forEach(content ->
                log.info("{} - 마감일: {}", content.getContentTitle(), content.getEndDate())
        );

        verify(contentRepository).findFilteredContents(any(), any(), any(), any(), any(), eq(false), eq(pageable));
    }

    @Test
    @DisplayName("4. 기본값 테스트 (가까운순이 기본값)")
    void testDefaultSort() {
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest();
        Pageable pageable = PageRequest.of(0, 5);
        Page<Content> mockPage = new PageImpl<>(mockContents.subList(0, 3), pageable, 3);

        given(contentRepository.findFilteredContentsByDistance(
                any(), any(), any(), any(), any(), eq(37.4981), eq(127.0276),  eq(false),eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        String expectedSort = request.getSortBy() != null ? request.getSortBy() : "distance";
        assertThat(expectedSort).isEqualTo("distance");

        log.info("========== 기본값 테스트 (가까운순) ==========");
        log.info("정렬 방식: " + expectedSort + " (기본값)");
        log.info("총 개수: {}",result.getTotalElements());
        result.getContent().forEach(content ->
                log.info("{} - 시작일: {} (북마크: {})", content.getContentTitle(), content.getStartDate(), content.getBookmarkCount())

        );

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), any(), eq(37.4981), eq(127.0276),  eq(false),eq(pageable));
    }

    @Test
    @DisplayName("5. 사용자 위치 없음 시 fallback 테스트")
    void testDistanceSortFallback() {
        setupNoLocationMock();

        ContentFilterRequest request = new ContentFilterRequest();
        request.setSortBy("distance");

        Pageable fallbackPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "endDate"));

        List<Content> sortedByStartDate = mockContents.stream()
                .sorted(Comparator.comparing(Content::getStartDate))
                .toList();

        Page<Content> mockPage = new PageImpl<>(sortedByStartDate, fallbackPageable, sortedByStartDate.size());

        given(contentRepository.findFilteredContents(
                any(), any(), any(), any(), any(), eq(false), eq(fallbackPageable)
        )).willReturn(mockPage);

        // when
        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, fallbackPageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        for (int i = 0; i < result.getContent().size() - 1; i++) {
            LocalDate current = result.getContent().get(i).getEndDate();
            LocalDate next = result.getContent().get(i + 1).getEndDate();
            assertThat(current).isBeforeOrEqualTo(next);
        }

        log.info("========== 위치 정보 없음 - fallback 테스트 ==========");
        log.info("요청 정렬: distance → 실제 정렬: startDate (fallback)");
        result.getContent().forEach(content ->
                log.info("{} - 종료일: {}", content.getContentTitle(), content.getEndDate())
        );

        verify(contentRepository).findFilteredContents(
                any(), any(), any(), any(), any(), eq(false), eq(fallbackPageable));
        verify(contentRepository, never()).findFilteredContentsByDistance(
                any(), any(), any(), any(), any(), anyDouble(), anyDouble(), anyBoolean(), any());
    }


    @Test
    @DisplayName("키워드 검색 테스트")
    void testKeywordSearch() {
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest();
        request.setKeyword("음악");

        Pageable pageable = PageRequest.of(0, 10);

        List<Content> keywordResults = mockContents.stream()
                .filter(content -> content.getContentTitle().contains("음악"))
                .toList();
        Page<Content> mockPage = new PageImpl<>(keywordResults, pageable, keywordResults.size());

        given(contentRepository.findFilteredContentsByDistance(
                any(), any(), any(), any(), eq("음악"),
                eq(37.4981), eq(127.0276), eq(false), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();

        log.info("========== 키워드 검색 테스트 ==========");
        log.info("검색어: 음악");
        result.getContent().forEach(content ->
                log.info("{} - 검색어 포함 확인",content.getContentTitle()));

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), eq("음악"),
                eq(37.4981), eq(127.0276), eq(false), eq(pageable));
    }

    @Test
    @DisplayName("키워드 검색 테스트 - 카테고리 한글 검색")
    void testKeywordSearchByKoreanCategory() {
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest();
        request.setKeyword("무용");

        Pageable pageable = PageRequest.of(0, 10);

        List<Content> keywordResults = mockContents.stream()
                .filter(content -> content.getCategory().getCategory() == ContentClassification.DANCE)
                .toList();
        Page<Content> mockPage = new PageImpl<>(keywordResults, pageable, keywordResults.size());

        given(contentRepository.findFilteredContentsByDistance(
                any(), any(), any(), any(), eq("무용"),
                eq(37.4981), eq(127.0276), eq(false), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();

        log.info("========== 키워드 검색 테스트 (카테고리-한글) ==========");
        log.info("검색어: 무용");
        result.getContent().forEach(content ->
                log.info("{} - 카테고리: {}",content.getContentTitle(), content.getCategory()));

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), eq("무용"),
                eq(37.4981), eq(127.0276), eq(false), eq(pageable));
    }

    @Test
    @DisplayName("키워드 검색 테스트 - 주소 검색")
    void testKeywordSearchByAddress() {
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest();
        request.setKeyword("예술의전당");

        Pageable pageable = PageRequest.of(0, 10);

        List<Content> updatedContents = mockContents.stream()
                .peek(content -> {
                    if (content.getId() == 1L) {
                        content.setAddress("예술의전당 콘서트홀");
                    }
                })
                .filter(content -> content.getAddress() != null && content.getAddress().contains("예술의전당"))
                .toList();

        Page<Content> mockPage = new PageImpl<>(updatedContents, pageable, updatedContents.size());

        given(contentRepository.findFilteredContentsByDistance(
                any(), any(), any(), any(), eq("예술의전당"),
                eq(37.4981), eq(127.0276), eq(false), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentListDTO> result = contentService.findByFiltersWithSort(mockUser.getEmail(), request, pageable);

        assertThat(result).isNotNull();

        log.info("========== 키워드 검색 테스트 (주소) ==========");
        log.info("검색어: 예술의전당");
        result.getContent().forEach(content ->
                log.info("{} - 주소: {}", content.getContentTitle(),content.getAddress()));

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), eq("예술의전당"),
                eq(37.4981), eq(127.0276), eq(false), eq(pageable));
    }

    private List<Content> createMockContents() {
        ContentCategory category1 = new ContentCategory();
        category1.setCategory(ContentClassification.DANCE);

        ContentCategory category2 = new ContentCategory();
        category2.setCategory(ContentClassification.POP_MUSIC);

        Content content1 = new Content();
        content1.setId(1L);
        content1.setContentTitle("춤추는 별들");
        content1.setBookmarkCount(10);
        content1.setStartDate(LocalDate.of(2025, 8, 1));
        content1.setEndDate(LocalDate.of(2025, 8, 31));
        content1.setGuname("강남구");
        content1.setAddress("서울특별시 강남구 테헤란로 123 문화센터");
        content1.setLatitude(37.5000);
        content1.setLongitude(127.0300);
        content1.setCategory(category1);
        content1.setImages(Collections.emptyList());
        content1.setUrls(Collections.emptyList());

        Content content2 = new Content();
        content2.setId(2L);
        content2.setContentTitle("음악의 향연");
        content2.setBookmarkCount(25);
        content2.setStartDate(LocalDate.of(2025, 7, 15));
        content2.setEndDate(LocalDate.of(2025, 7, 30));
        content2.setGuname("서초구");
        content2.setAddress("서울특별시 서초구 예술의전당로 456");
        content2.setLatitude(37.4800);
        content2.setLongitude(127.0200);
        content2.setCategory(category2);
        content2.setImages(Collections.emptyList());
        content2.setUrls(Collections.emptyList());

        Content content3 = new Content();
        content3.setId(3L);
        content3.setContentTitle("발레 갈라쇼");
        content3.setBookmarkCount(5);
        content3.setStartDate(LocalDate.of(2025, 9, 1));
        content3.setEndDate(LocalDate.of(2025, 9, 15));
        content3.setGuname("강남구");
        content3.setAddress("서울특별시 강남구 압구정로 789 아트홀");
        content3.setLatitude(37.5100);
        content3.setLongitude(127.0400);
        content3.setCategory(category1);
        content3.setImages(Collections.emptyList());
        content3.setUrls(Collections.emptyList());

        return Arrays.asList(content1, content3, content2);
    }
}