package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.*;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
    public ContentDetailDTO getContents(final Long id) {
        Content content = contentRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        return toDetailDTO(content);
    }

    // 컨텐츠 필터링(컨텐츠 조회)
    public Page<ContentListDTO> findByFiltersWithSort(String userEmail, ContentFilterRequest request, Pageable pageable) {
        try {
            Page<Content> contents;
            if (request.isBookmarkSort()) {
                log.info("sortBy: {}", request.getSortBy());
                contents = findByFiltersOrderByBookmark(request, pageable);
            } else if (request.isEndDateSort()) {
                contents = findByFiltersOrderByEndDate(request, pageable);
            } else {
                contents = findByFiltersOrderByDistance(userEmail, request, pageable);
            }

            if (contents.isEmpty()) {
                log.info("필터 결과 없음 - 요청 필터: {}", request);
            }

            log.info("조회된 컨텐츠 수: {}", contents.getTotalElements());
            return contents.map(this::toContentListDTO);

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

        Sort sort = Sort.by(Sort.Direction.DESC, "bookmarkCount");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        log.info("생성된 정렬: {}", sortedPageable.getSort());
        
        return contentRepository.findFilteredContents(
                request.getCategory(),
                request.getGuname(),
                request.getStartDate(),
                request.getEndDate(),
                request.getKeyword(),
                false,
                sortedPageable
        );
    }

    // 마감 임박순 정렬
    private Page<Content> findByFiltersOrderByEndDate(ContentFilterRequest request, Pageable pageable) {

        Sort sort = Sort.by(Sort.Direction.ASC, "endDate");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return contentRepository.findFilteredContents(
                request.getCategory(),
                request.getGuname(),
                request.getStartDate(),
                request.getEndDate(),
                request.getKeyword(),
                false,
                sortedPageable
        );
    }

    // 사용자 기본 위치 조회
    private Double[] getUserDefaultLocation(String userEmail) {
        try {
            if ("anonymousUser".equals(userEmail)) {
                return new Double[]{null, null};
            }

            UserDTO user = userService.get(userEmail);
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
    private Page<Content> findByFiltersOrderByDistance(String userEmail, ContentFilterRequest request, Pageable pageable) {
        Double[] userLocation = getUserDefaultLocation(userEmail);
        Double userLat = userLocation[0];
        Double userLng = userLocation[1];

        if (userLat == null || userLng == null) {
            log.warn("사용자 위치 정보를 확인할 수 없어 '마감 임박순'으로 정렬 방식을 변경합니다.");

            Pageable endDateSortedPageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "endDate")
            );

            return findByFiltersOrderByEndDate(request, endDateSortedPageable);
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
    public List<ContentSimpleDTO> findNearbyContents(Long id, double radiusInKm, int limit, boolean includeExpired) {
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
                    .map(this::toSimpleDTO)
                    .toList();
        } catch (Exception e) {
            log.error("주변 컨텐츠 조회 실패: contentId={}, lat={}, lng={}", id, latitude, longitude, e);
            return Collections.emptyList();
        }
    }

    // 카테고리별 컨텐츠 노출
    @Transactional(readOnly = true)
    public List<ContentSimpleDTO> findRandomByCategory(Long id, int limit, boolean includeExpired) {
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
                .map(this::toSimpleDTO)
                .toList();
    }

    private ContentSimpleDTO toSimpleDTO(Content content) {
        return modelMapper.map(content, ContentSimpleDTO.class); }

    private ContentListDTO toContentListDTO(Content content) {
        return modelMapper.map(content, ContentListDTO.class); }

    private ContentDetailDTO toDetailDTO(Content content) {
        return modelMapper.map(content, ContentDetailDTO.class);
    }

    public List<ContentWithReasonDTO> findByIds(List<Long> recommendIds) {
        List<Content> contents = contentRepository.findContentsByIdsWithAllRelations(recommendIds);

        return contents.stream().map(this::toReasonDTO).toList();

    }

    private ContentWithReasonDTO toReasonDTO(Content content) {
        return ContentWithReasonDTO.builder()
                         .id(content.getId())
                         .contentTitle(content.getContentTitle())
                         .startDate(content.getStartDate())
                         .endDate(content.getEndDate())
                         .address(content.getAddress())
                         .poster(content.getPoster())
                         .eventType(content.getEventType())
                         .build();
    }
}
