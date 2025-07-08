package com.grepp.funfun.app.model.content.service;

import com.grepp.funfun.app.controller.api.content.payload.ContentFilterRequest;
import com.grepp.funfun.app.model.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.model.bookmark.repository.ContentBookmarkRepository;
import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.model.content.code.ContentClassification;
import com.grepp.funfun.app.model.content.dto.ContentDTO;
import com.grepp.funfun.app.model.content.dto.ContentImageDTO;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import com.grepp.funfun.app.model.content.entity.ContentImage;
import com.grepp.funfun.app.model.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final ModelMapper modelMapper;

    public ContentDTO get(final Long id) {
        Content content = contentRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        return mapToDTO(content, new ContentDTO());
    }

    // 컨텐츠 필터링
    @Transactional(readOnly = true)
    public Page<ContentDTO> findByFilters(ContentFilterRequest request, Pageable pageable) {
        Page<Content> contents = contentRepository.findFilteredContents(
                request.getCategory(),
                request.getGuName(),
                request.getStartDate(),
                request.getEndDate(),
                pageable
        );

        if (contents.isEmpty()) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
        return contents.map(content -> mapToDTO(content, new ContentDTO()));
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
                .map(c -> mapToDTO(c, new ContentDTO()))
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
                .filter(c -> !c.getId().equals(id)) // 자기 자신 제외
                .collect(Collectors.toList());

        Collections.shuffle(filtered);
        return filtered.stream()
                .limit(limit)
                .map(c -> mapToDTO(c, new ContentDTO()))
                .toList();
    }

    // 즐겨찾기순 정렬
//    public Page<ContentDTO> sortedByBookmarkCount(Pageable pageable) {
//        Page<Content> contents = contentRepository.findAll(pageable);
//        return contents.map(content -> mapToDTO(content, new ContentDTO()));
//    }

    // view
    @Transactional(readOnly = true)
    public List<Content> findAll() {
        return contentRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
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
        contentDTO.setStartDate(content.getStartDate());
        contentDTO.setEndDate(content.getEndDate());
        contentDTO.setStatus(content.getStatus());
        contentDTO.setFee(content.getFee());
        contentDTO.setAddress(content.getAddress());
        contentDTO.setReservationUrl(content.getReservationUrl());
        contentDTO.setGuName(content.getGuName());
        contentDTO.setRunTime(content.getRunTime());
        contentDTO.setStartTime(content.getStartTime());
        contentDTO.setEndTime(content.getEndTime());
        contentDTO.setBookmarkCount(content.getBookmarkCount());
        contentDTO.setCategory(content.getCategory() == null ? null : content.getCategory().getId());

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
        content.setStartDate(contentDTO.getStartDate());
        content.setEndDate(contentDTO.getEndDate());
        content.setStatus(contentDTO.getStatus());
        content.setFee(contentDTO.getFee());
        content.setAddress(contentDTO.getAddress());
        content.setReservationUrl(contentDTO.getReservationUrl());
        content.setGuName(contentDTO.getGuName());
        content.setRunTime(contentDTO.getRunTime());
        content.setStartTime(contentDTO.getStartTime());
        content.setEndTime(contentDTO.getEndTime());
        content.setBookmarkCount(contentDTO.getBookmarkCount() != null ? contentDTO.getBookmarkCount() : 0);
        final ContentCategory category = contentDTO.getCategory() == null ? null : contentCategoryRepository.findById(contentDTO.getCategory())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        content.setCategory(category);

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
