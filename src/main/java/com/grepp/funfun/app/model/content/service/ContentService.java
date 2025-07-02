package com.grepp.funfun.app.model.content.service;

import com.grepp.funfun.app.model.bookmark.entity.ContentBookMark;
import com.grepp.funfun.app.model.bookmark.repository.ContentBookMarkRepository;
import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.model.content.dto.ContentDTO;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import com.grepp.funfun.app.model.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentBookMarkRepository contentBookMarkRepository;
    private final CalendarRepository calendarRepository;

    public ContentService(final ContentRepository contentRepository,
            final ContentCategoryRepository contentCategoryRepository,
            final ContentBookMarkRepository contentBookMarkRepository,
            final CalendarRepository calendarRepository) {
        this.contentRepository = contentRepository;
        this.contentCategoryRepository = contentCategoryRepository;
        this.contentBookMarkRepository = contentBookMarkRepository;
        this.calendarRepository = calendarRepository;
    }

    public List<ContentDTO> findAll() {
        final List<Content> contents = contentRepository.findAll(Sort.by("id"));
        return contents.stream()
                .map(content -> mapToDTO(content, new ContentDTO()))
                .toList();
    }

    public ContentDTO get(final Long id) {
        return contentRepository.findById(id)
                .map(content -> mapToDTO(content, new ContentDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
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
        final ContentBookMark contentContentBookMark = contentBookMarkRepository.findFirstByContent(content);
        if (contentContentBookMark != null) {
            referencedWarning.setKey("content.contentBookMark.content.referenced");
            referencedWarning.addParam(contentContentBookMark.getId());
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
