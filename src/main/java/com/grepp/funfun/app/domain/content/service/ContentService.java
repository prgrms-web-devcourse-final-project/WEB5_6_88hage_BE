package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.ContentUrlDTO;
import com.grepp.funfun.app.domain.content.dto.payload.ContentFilterRequest;
import com.grepp.funfun.app.domain.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.domain.bookmark.repository.ContentBookmarkRepository;
import com.grepp.funfun.app.domain.calendar.entity.Calendar;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.content.entity.ContentUrl;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.entity.ContentImage;
import com.grepp.funfun.app.domain.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import com.grepp.funfun.app.delete.util.ReferencedWarning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentBookmarkRepository contentBookmarkRepository;
    private final CalendarRepository calendarRepository;

    public ContentDTO getContents(final Long id) {
        Content content = contentRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        return ContentDTO.builder()
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

    // 컨텐츠 필터링
    @Transactional(readOnly = true)
    public Page<ContentDTO> findByFilters(ContentFilterRequest request, Pageable pageable) {
        Page<Content> contents = contentRepository.findFilteredContents(
                request.getCategory(),
                request.getGuname(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        if (contents.isEmpty()) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
        return contents.map(this::toDTO);
    }

    // 거리순 컨텐츠 노출
    @Transactional(readOnly = true)
    public List<ContentDTO> findNearbyContents(Long id, double radiusInKm, int limit){
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        double lat = content.getLatitude();
        double lng = content.getLongitude();

        if (lat == 0.0 || lng == 0.0) {
            return Collections.emptyList();
        }

        List<Content> nearby = contentRepository.findNearby(lat, lng, radiusInKm, id, limit);

        return nearby.stream()
                .map(this::toDTO)
                .toList();

    }

    // 카테고리별 컨텐츠 노출
    @Transactional(readOnly = true)
    public List<ContentDTO> findRandomByCategory(Long id, int limit){
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        ContentClassification category = content.getCategory().getCategory();

        List<Content> sameCategoryContents = contentRepository.findByCategoryCategory(category);

        List<Content> filtered = sameCategoryContents.stream()
                .filter(c -> !c.getId().equals(id))
                .collect(Collectors.toList());

        return filtered.stream()
                .limit(limit)
                .map(this::toDTO)
                .toList();
    }

    private ContentDTO toDTO(Content content) {
        return ContentDTO.builder()
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

    // view
    @Transactional(readOnly = true)
    public List<Content> findAll() {
        return contentRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
    }

    @Transactional(readOnly = true)
    public ContentDTO get(final Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        return mapToDTO(content, new ContentDTO());
    }


    @Transactional
    public void delete(final Long id) {
        contentRepository.deleteById(id);
    }

    @Transactional
    public Long create(final ContentDTO contentDTO) {
        final Content content = new Content();
        mapToEntity(contentDTO, content);
        return contentRepository.save(content).getId();
    }
    @Transactional
    public void update(final Long id, final ContentDTO contentDTO) {
        final Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(contentDTO, content);
        contentRepository.save(content);
    }

    private ContentDTO mapToDTO(final Content content, final ContentDTO contentDTO) {
        contentDTO.setId(content.getId());
        contentDTO.setContentTitle(content.getContentTitle());
        contentDTO.setAge(content.getAge());
        contentDTO.setStartDate(content.getStartDate());
        contentDTO.setEndDate(content.getEndDate());
        contentDTO.setFee(content.getFee());
        contentDTO.setAddress(content.getAddress());
        contentDTO.setGuname(content.getGuname());
        contentDTO.setRunTime(content.getRunTime());
        contentDTO.setStartTime(content.getStartTime());
        contentDTO.setPoster(content.getPoster());
        contentDTO.setBookmarkCount(content.getBookmarkCount());
        contentDTO.setCategory(content.getCategory() != null ? content.getCategory().getCategory().name() : null);

        if (content.getImages() != null) {
            List<ContentImageDTO> imageDTOs = content.getImages().stream()
                    .map(img -> {
                        ContentImageDTO dto = new ContentImageDTO();
                        dto.setId(img.getId());
                        dto.setImageUrl(img.getImageUrl());
                        return dto;
                    })
                    .toList();
            contentDTO.setImages(imageDTOs);
        }

        return contentDTO;
    }

    private Content mapToEntity(final ContentDTO contentDTO, final Content content) {
        content.setContentTitle(contentDTO.getContentTitle());
        content.setAge(contentDTO.getAge());
        content.setStartDate(contentDTO.getStartDate());
        content.setEndDate(contentDTO.getEndDate());
        content.setFee(contentDTO.getFee());
        content.setAddress(contentDTO.getAddress());
        content.setGuname(contentDTO.getGuname());
        content.setRunTime(contentDTO.getRunTime());
        content.setStartTime(contentDTO.getStartTime());
        content.setPoster(contentDTO.getPoster());
        content.setBookmarkCount(contentDTO.getBookmarkCount() != null ? contentDTO.getBookmarkCount() : 0);
        if (contentDTO.getCategory() != null) {
            content.setCategory(null);
        }
        if (contentDTO.getImages() != null) {
            List<ContentImage> imageEntities = contentDTO.getImages().stream()
                    .map(imgDto -> {
                        ContentImage img = new ContentImage();
                        img.setImageUrl(imgDto.getImageUrl());
                        img.setContent(content);
                        return img;
                    })
                    .toList();
            content.setImages(imageEntities);
        }
        return content;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        final ContentBookmark contentContentBookmark = contentBookmarkRepository.findFirstByContent(content);
        if (contentContentBookmark != null) {
            referencedWarning.setKey("content.contentBookmark.content.referenced");
            referencedWarning.addParam(contentContentBookmark.getId());
            return referencedWarning;
        }
        final Calendar contentCalendar = calendarRepository.findFirstByContent(content);
        if (contentCalendar != null) {
            referencedWarning.setKey("content.calendar.content.referenced");
            referencedWarning.addParam(contentCalendar.getId());
            return referencedWarning;
        }
        return null;
    }


}
