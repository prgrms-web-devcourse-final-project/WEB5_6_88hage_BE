package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import com.grepp.funfun.app.domain.content.dto.ContentUrlDTO;
import com.grepp.funfun.app.domain.content.dto.ContentWithReasonDTO;
import com.grepp.funfun.app.domain.content.dto.payload.ContentFilterRequest;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.user.dto.UserDTO;
import com.grepp.funfun.app.domain.user.service.UserService;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    // 컨텐츠 상세 조회
    public ContentDTO getContents(final Long id) {
        Content content = contentRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        return toDTO(content);
    }

    // 컨텐츠 필터링(컨텐츠 조회)
    public Page<ContentDTO> findByFiltersWithSort(ContentFilterRequest request, Pageable pageable) {
        try {
            Page<Content> contents;
            if (request.isBookmarkSort()) {
                log.info("sortBy: {}", request.getSortBy());
                contents = findByFiltersOrderByBookmark(request, pageable);
            } else if (request.isEndDateSort()) {
                contents = findByFiltersOrderByEndDate(request, pageable);
            } else {
                contents = findByFiltersOrderByDistance(request, pageable);
            }

            if (contents.isEmpty()) {
                log.info("필터 결과 없음 - 요청 필터: {}", request);
            }

            log.info("조회된 컨텐츠 수: {}", contents.getTotalElements());
            return contents.map(this::toDTO);

        } catch (CommonException e) {
            log.error("NOT_FOUND 예외 발생 - 필터링 조건: {}, 메시지: {}", request, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("알 수 없는 오류 발생 - 요청 필터: {}, 에러: {}", request, e.getMessage(), e);
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 북마크순 정렬
    private Page<Content> findByFiltersOrderByBookmark(ContentFilterRequest request, Pageable pageable) {

        log.info("생성된 sortedPageable: {}", pageable.getSort());
        log.info("북마크순 정렬을 위해 repository 호출");

        Page<Content> result = contentRepository.findFilteredContents(
                request.getCategory(),
                request.getGuname(),
                request.getStartDate(),
                request.getEndDate(),
                request.getKeyword(),
                false,
                pageable
        );

        log.info("repository에서 반환된 결과 개수: {}", result.getContent().size());
        if (!result.getContent().isEmpty()) {
            Content first = result.getContent().get(0);
            log.info("첫 번째 결과 - ID: {}, bookmarkCount: {}", first.getId(), first.getBookmarkCount());
            if (result.getContent().size() > 1) {
                Content second = result.getContent().get(1);
                log.info("두 번째 결과 - ID: {}, bookmarkCount: {}", second.getId(), second.getBookmarkCount());
            }
        }

        return result;
    }

    // 마감 임박순 정렬
    private Page<Content> findByFiltersOrderByEndDate(ContentFilterRequest request, Pageable pageable) {


        return contentRepository.findFilteredContents(
                request.getCategory(),
                request.getGuname(),
                request.getStartDate(),
                request.getEndDate(),
                request.getKeyword(),
                false,
                pageable
        );
    }

    // 사용자 기본 위치 조회
    private Double[] getUserDefaultLocation() {
        try {
            String currentUserEmail = SecurityContextHolder.getContext()
                    .getAuthentication().getName();

            UserDTO user = userService.get(currentUserEmail);
            if (user != null && user.getLatitude() != null && user.getLongitude() != null) {
                log.info("사용자 기본 위치 조회 : 위도={}, 경도={}",
                        user.getLatitude(), user.getLongitude());
                return new Double[]{user.getLatitude(), user.getLongitude()};
            } else {
                log.warn("사용자의 기본 위치 정보가 존재하지 않습니다.");
                return new Double[]{null, null};
            }
        } catch (Exception e) {
            log.warn("사용자 기본 위치 조회 중 오류 발생: {}", e.getMessage());
            return new Double[]{null, null};
        }
    }

    // 가까운순 정렬
    private Page<Content> findByFiltersOrderByDistance(ContentFilterRequest request, Pageable pageable) {
        Double[] userLocation = getUserDefaultLocation();
        Double userLat = userLocation[0];
        Double userLng = userLocation[1];

        if (userLat == null || userLng == null) {
            log.warn("사용자 위치 정보를 확인할 수 없어 '마감 임박순'으로 정렬 방식을 변경합니다.");
            return findByFiltersOrderByEndDate(request, pageable);
        }

        return contentRepository.findFilteredContentsByDistance(
                request.getCategory(),
                request.getGuname(),
                request.getStartDate(),
                request.getEndDate(),
                request.getKeyword(),
                userLat,
                userLng,
                false,
                pageable
        );
    }

    // 거리순 컨텐츠 노출
    public List<ContentDTO> findNearbyContents(Long id, double radiusInKm, int limit, boolean includeExpired) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        Double latitude = content.getLatitude();
        Double longitude = content.getLongitude();

        if (latitude == null || longitude == null || latitude == 0.0 || longitude == 0.0) {
            log.warn("위경도 정보가 없는 컨텐츠: {}", id);
            return Collections.emptyList();
        }

        try {
            List<Content> nearby = contentRepository.findNearby(latitude, longitude, radiusInKm, id, limit, includeExpired);
            return nearby.stream()
                    .map(this::toDTO)
                    .toList();
        } catch (Exception e) {
            log.error("주변 컨텐츠 조회 실패: contentId={}, lat={}, lng={}", id, latitude, longitude, e);
            return Collections.emptyList();
        }
    }

    // 카테고리별 컨텐츠 노출
    @Transactional(readOnly = true)
    public List<ContentDTO> findRandomByCategory(Long id, int limit, boolean includeExpired) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        ContentClassification category = content.getCategory().getCategory();

        List<Content> sameCategoryContents = contentRepository.findByCategoryCategory(category, includeExpired);

        List<Content> filtered = sameCategoryContents.stream()
                .filter(c -> !c.getId().equals(id))
                .collect(Collectors.toList());

        Collections.shuffle(filtered);

        return filtered.stream()
                .limit(limit)
                .map(this::toDTO)
                .toList();
    }

    private ContentDTO toDTO(Content content) {
        return modelMapper.map(content, ContentDTO.class);
    }

    public List<ContentWithReasonDTO> findByIds(List<Long> recommendIds) {
        List<Content> contents = contentRepository.findContentsByIdsWithAllRelations(recommendIds);

        return contents.stream().map(this::toReasonDTO).toList();

    }

    private ContentWithReasonDTO toReasonDTO(Content content) {
        return ContentWithReasonDTO.builder()
                         .id(content.getId())
                         .contentTitle(content.getContentTitle())
                         .age(content.getAge())
                         .startDate(content.getStartDate())
                         .endDate(content.getEndDate())
                         .fee(content.getFee())
                         .address(content.getAddress())
                         .guname(content.getGuname())
                         .time(content.getTime())
                         .runTime(content.getRunTime())
                         .startTime(content.getStartTime())
                         .poster(content.getPoster())
                         .description(content.getDescription())
                         .bookmarkCount(content.getBookmarkCount())
                         .eventType(content.getEventType())
                         .category(content.getCategory() != null ? content.getCategory().getCategory().name() : null)
                         .images(content.getImages().stream()
                                        .map(img -> ContentImageDTO.builder()
                                                                   .id(img.getId())
                                                                   .imageUrl(img.getImageUrl())
                                                                   .build())
                                        .toList())
                         .urls(content.getUrls().stream()
                                      .map(url -> ContentUrlDTO.builder()
                                                               .id(url.getId())
                                                               .siteName(url.getSiteName())
                                                               .url(url.getUrl())
                                                               .build())
                                      .toList())
                         .build();
    }
}
