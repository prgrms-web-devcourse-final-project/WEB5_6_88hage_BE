package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.payload.ContentFilterRequest;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.user.dto.UserDTO;
import com.grepp.funfun.app.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @InjectMocks
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private UserDTO mockUser;
    private List<Content> mockContents;

    @BeforeEach
    void setUp() {
        // 테스트용 컨텐츠 데이터 생성
        mockContents = createMockContents();
    }

    private void setupDistanceSortMocks() {
        // Security Context 모킹
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getName()).willReturn("test@example.com");

        // 테스트용 사용자 기본 위치 설정 (서울 강남역 기준)
        mockUser = new UserDTO();
        mockUser.setEmail("test@example.com");
        mockUser.setLatitude(37.4981);
        mockUser.setLongitude(127.0276);

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
                any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(3);

        System.out.println("========== 전체 조회 (기본 정렬: 가까운순) ===========");
        System.out.println("총 개수: " + result.getTotalElements());
        System.out.println("정렬 방식: distance (기본값)");

        result.getContent().forEach(content ->
                System.out.println(content.getId() + " - " + content.getContentTitle() +
                        " (북마크: " + content.getBookmarkCount() + ")")
        );

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(pageable));
    }

    @Test
    @DisplayName("무용 카테고리 필터링 테스트")
    public void getFilterByCategory(){
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest();
        request.setCategory(ContentClassification.DANCE);
        Pageable pageable = PageRequest.of(0, 10);

        List<Content> danceContents = mockContents.stream()
                .filter(content -> content.getCategory().getCategory() == ContentClassification.DANCE)
                .toList();
        Page<Content> mockPage = new PageImpl<>(danceContents, pageable, danceContents.size());

        given(contentRepository.findFilteredContentsByDistance(
                eq(ContentClassification.DANCE), any(), any(), any(), eq(37.4981), eq(127.0276), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();

        System.out.println("============= 무용 카테고리 필터링 ============");
        if (!result.getContent().isEmpty()) {
            result.getContent().forEach(content ->
                    System.out.println(content.getContentTitle() + " - 카테고리: " + content.getCategory())
            );
            result.getContent().forEach(content ->
                    assertThat(content.getCategory()).isEqualTo("DANCE")
            );
        } else {
            System.out.println("무용 카테고리 컨텐츠가 없습니다.");
        }
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
                any(), eq("강남구"), any(), any(), eq(37.4981), eq(127.0276), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();
        if (!result.getContent().isEmpty()) {
            assertThat(result.getContent()).allMatch(content ->
                    "강남구".equals(content.getGuname())
            );

            System.out.println("======= 강남구 지역 필터링 =======");
            result.getContent().forEach(content ->
                    System.out.println(content.getContentTitle() + " - 지역: " + content.getGuname())
            );
        } else {
            System.out.println("강남구 컨텐츠가 없습니다.");
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
                any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        System.out.println("========== 가까운순 정렬 (사용자 위치 기준) ==========");
        System.out.println("기준 위치: 강남역 (37.4981, 127.0276)");
        System.out.println("총 개수: " + result.getTotalElements());

        result.getContent().forEach(content -> {
            Double lat = content.getLatitude();
            Double lng = content.getLongitude();
            String location = (lat != null && lng != null) ?
                    String.format("(%.4f, %.4f)", lat, lng) : "위치정보없음";
            System.out.println(content.getContentTitle() + " - " + location);
        });

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(pageable));
    }

    @Test
    @DisplayName("2. 북마크순 정렬 테스트")
    void testBookmarkCountDescSort() {
        // given
        ContentFilterRequest request = new ContentFilterRequest();
        request.setSortBy("bookmarkCount");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Content> mockPage = new PageImpl<>(mockContents, pageable, mockContents.size());

        given(contentRepository.findFilteredContents(
                any(), any(), any(), any(),
                argThat(p -> p.getSort().equals(Sort.by(Sort.Direction.DESC, "bookmarkCount")))))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        System.out.println("========== 북마크순 정렬 테스트 (Service 로직 검증) ==========");
        System.out.println("Service가 Repository에 전달한 정렬 조건 확인");
        System.out.println("총 개수: " + result.getTotalElements());
        result.getContent().forEach(content ->
                System.out.println(content.getContentTitle() +
                        " - 북마크: " + content.getBookmarkCount())
        );

        verify(contentRepository).findFilteredContents(
                any(), any(), any(), any(),
                argThat(p -> {
                    Sort sort = p.getSort();
                    boolean isCorrectSort = sort.isSorted() &&
                            sort.getOrderFor("bookmarkCount") != null &&
                            sort.getOrderFor("bookmarkCount").getDirection() == Sort.Direction.DESC;

                    System.out.println("✅ Service가 올바른 정렬 조건을 전달했는지: " + isCorrectSort);
                    return isCorrectSort;
                }));
    }

    @Test
    @DisplayName("3. 개최 임박순 정렬 테스트 (가까운 날짜 순)")
    void testStartDateAscSort() {
        ContentFilterRequest request = new ContentFilterRequest();
        request.setSortBy("startDate");
        Pageable pageable = PageRequest.of(0, 10);

        List<Content> sortedByStartDate = mockContents.stream()
                .sorted((c1, c2) -> c1.getStartDate().compareTo(c2.getStartDate()))
                .toList();
        Page<Content> mockPage = new PageImpl<>(sortedByStartDate, pageable, sortedByStartDate.size());

        Pageable sortedPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "startDate"));
        given(contentRepository.findFilteredContents(any(), any(), any(), any(), eq(sortedPageable)))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        for (int i = 0; i < result.getContent().size() - 1; i++) {
            LocalDate current = result.getContent().get(i).getStartDate();
            LocalDate next = result.getContent().get(i + 1).getStartDate();
            assertThat(current).isBeforeOrEqualTo(next);
        }

        System.out.println("========== 개최 임박순 정렬 (가까운 날짜 순) ==========");
        System.out.println("총 개수: " + result.getTotalElements());
        result.getContent().forEach(content ->
                System.out.println(content.getContentTitle() +
                        " - 시작일: " + content.getStartDate())
        );

        verify(contentRepository).findFilteredContents(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("4. 기본값 테스트 (가까운순이 기본값)")
    void testDefaultSort() {
        setupDistanceSortMocks();
        ContentFilterRequest request = new ContentFilterRequest(); // sortBy 설정 안함
        Pageable pageable = PageRequest.of(0, 5);
        Page<Content> mockPage = new PageImpl<>(mockContents.subList(0, 3), pageable, 3);

        given(contentRepository.findFilteredContentsByDistance(
                any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(pageable)))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        String expectedSort = request.getSortBy() != null ? request.getSortBy() : "distance";
        assertThat(expectedSort).isEqualTo("distance");

        System.out.println("========== 기본값 테스트 (가까운순) ==========");
        System.out.println("정렬 방식: " + expectedSort + " (기본값)");
        System.out.println("총 개수: " + result.getTotalElements());
        result.getContent().forEach(content ->
                System.out.println(content.getContentTitle() +
                        " - 시작일: " + content.getStartDate() +
                        " (북마크: " + content.getBookmarkCount() + ")")
        );

        verify(contentRepository).findFilteredContentsByDistance(
                any(), any(), any(), any(), eq(37.4981), eq(127.0276), eq(pageable));
    }

    @Test
    @DisplayName("5. 사용자 위치 없음 시 fallback 테스트")
    void testDistanceSortFallback() {
        UserDTO userWithoutLocation = new UserDTO();
        userWithoutLocation.setEmail("test@example.com");
        userWithoutLocation.setLatitude(null);
        userWithoutLocation.setLongitude(null);
        given(userService.get("test@example.com")).willReturn(userWithoutLocation);

        ContentFilterRequest request = new ContentFilterRequest();
        request.setSortBy("distance");
        Pageable pageable = PageRequest.of(0, 5);

        List<Content> sortedByStartDate = mockContents.stream()
                .sorted((c1, c2) -> c1.getStartDate().compareTo(c2.getStartDate()))
                .toList();
        Page<Content> mockPage = new PageImpl<>(sortedByStartDate, pageable, sortedByStartDate.size());

        Pageable fallbackPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "startDate"));
        given(contentRepository.findFilteredContents(any(), any(), any(), any(), eq(fallbackPageable)))
                .willReturn(mockPage);

        Page<ContentDTO> result = contentService.findByFiltersWithSort(request, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();

        for (int i = 0; i < result.getContent().size() - 1; i++) {
            LocalDate current = result.getContent().get(i).getStartDate();
            LocalDate next = result.getContent().get(i + 1).getStartDate();
            assertThat(current).isBeforeOrEqualTo(next);
        }

        System.out.println("========== 위치 정보 없음 - fallback 테스트 ==========");
        System.out.println("요청 정렬: distance → 실제 정렬: startDate (fallback)");
        System.out.println("총 개수: " + result.getTotalElements());
        result.getContent().forEach(content ->
                System.out.println(content.getContentTitle() +
                        " - 시작일: " + content.getStartDate())
        );

        verify(contentRepository).findFilteredContents(any(), any(), any(), any(), any());
        verify(contentRepository, never()).findFilteredContentsByDistance(
                any(), any(), any(), any(), anyDouble(), anyDouble(), any());
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
        content3.setLatitude(37.5100);
        content3.setLongitude(127.0400);
        content3.setCategory(category1);
        content3.setImages(Collections.emptyList());
        content3.setUrls(Collections.emptyList());

        return Arrays.asList(content1, content2, content3);
    }
}