package com.grepp.funfun.app.model.content.service;

import com.grepp.funfun.app.controller.api.content.payload.ContentFilterRequest;
import com.grepp.funfun.app.model.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.model.bookmark.repository.ContentBookmarkRepository;
import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.model.content.code.ContentClassification;
import com.grepp.funfun.app.model.content.dto.ContentDTO;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import com.grepp.funfun.app.model.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentBookmarkRepository contentBookmarkRepository;
    private final CalendarRepository calendarRepository;

    public ContentService(final ContentRepository contentRepository,
            final ContentCategoryRepository contentCategoryRepository,
            final ContentBookmarkRepository contentBookmarkRepository,
            final CalendarRepository calendarRepository) {
        this.contentRepository = contentRepository;
        this.contentCategoryRepository = contentCategoryRepository;
        this.contentBookmarkRepository = contentBookmarkRepository;
        this.calendarRepository = calendarRepository;
    }

    public List<Content> findAll() {
        return contentRepository.findAll(Sort.by(Sort.Direction.ASC, "startDate"));
    }

    public ContentDTO get(final Long id) {
        Content content = contentRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        return mapToDTO(content, new ContentDTO());
    }

    public Long create(final ContentDTO contentDTO) {
        final Content content = new Content();
        mapToEntity(contentDTO, content);
        return contentRepository.save(content).getId();
    }
    public void update(final Long id, final ContentDTO contentDTO) {
        final Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(contentDTO, content);
        contentRepository.save(content);
    }

    public void delete(final Long id) {
        contentRepository.deleteById(id);
    }

    // 컨텐츠 필터링
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
    public List<ContentDTO> findNearbyContents(Long id, double radiusInKm, int limit){
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        
        double lat = content.getLatitude();
        double lng = content.getLongitude();
        
        List<Content> nearby = contentRepository.findNearby(lat, lng, radiusInKm, PageRequest.of(0, limit));
        
        return nearby.stream()
                .map(c -> mapToDTO(c, new ContentDTO()))
                .toList();
        
    }
    
    // 카테고리별 컨텐츠 노출
    public List<ContentDTO> findRandomByCategory(Long id, int limit){
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        ContentClassification category = content.getCategory().getCategory();

        List<Content> sameCategoryContents = contentRepository.findByCategory_Category(category);

        List<Content> filtered = sameCategoryContents.stream()
                .filter(c -> !c.getId().equals(id))
                .collect(Collectors.toList());

        Collections.shuffle(filtered);
        return filtered.stream()
                .limit(limit)
                .map(c -> mapToDTO(c, new ContentDTO()))
                .toList();
    }

    private ContentDTO mapToDTO(final Content content, final ContentDTO contentDTO) {
        contentDTO.setId(content.getId());
        contentDTO.setContentTitle(content.getContentTitle());
        contentDTO.setDescription(content.getDescription());
        contentDTO.setStartDate(content.getStartDate());
        contentDTO.setEndDate(content.getEndDate());
        contentDTO.setAddress(content.getAddress());
        contentDTO.setLatitude(content.getLatitude());
        contentDTO.setLongitude(content.getLongitude());
        contentDTO.setUrl(content.getUrl());
        contentDTO.setImageUrl(content.getImageUrl());
        contentDTO.setIsFree(content.getIsFree());
        contentDTO.setGuName(content.getGuName());
        contentDTO.setCategory(content.getCategory() == null ? null : content.getCategory().getId());
        return contentDTO;
    }

    private Content mapToEntity(final ContentDTO contentDTO, final Content content) {
        content.setContentTitle(contentDTO.getContentTitle());
        content.setDescription(contentDTO.getDescription());
        content.setStartDate(contentDTO.getStartDate());
        content.setEndDate(contentDTO.getEndDate());
        content.setAddress(contentDTO.getAddress());
        content.setLatitude(contentDTO.getLatitude());
        content.setLongitude(contentDTO.getLongitude());
        content.setUrl(contentDTO.getUrl());
        content.setImageUrl(contentDTO.getImageUrl());
        content.setIsFree(contentDTO.getIsFree());
        content.setGuName(contentDTO.getGuName());
        final ContentCategory category = contentDTO.getCategory() == null ? null : contentCategoryRepository.findById(contentDTO.getCategory())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        content.setCategory(category);
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
