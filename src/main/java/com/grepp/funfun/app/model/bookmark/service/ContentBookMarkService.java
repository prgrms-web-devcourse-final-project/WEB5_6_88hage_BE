package com.grepp.funfun.app.model.bookmark.service;

import com.grepp.funfun.app.model.bookmark.dto.ContentBookMarkDTO;
import com.grepp.funfun.app.model.bookmark.entity.ContentBookMark;
import com.grepp.funfun.app.model.bookmark.repository.ContentBookMarkRepository;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ContentBookMarkService {

    private final ContentBookMarkRepository contentBookMarkRepository;
    private final ContentRepository contentRepository;

    public ContentBookMarkService(final ContentBookMarkRepository contentBookMarkRepository,
            final ContentRepository contentRepository) {
        this.contentBookMarkRepository = contentBookMarkRepository;
        this.contentRepository = contentRepository;
    }

    public List<ContentBookMarkDTO> findAll() {
        final List<ContentBookMark> contentBookMarks = contentBookMarkRepository.findAll(Sort.by("id"));
        return contentBookMarks.stream()
                .map(contentBookMark -> mapToDTO(contentBookMark, new ContentBookMarkDTO()))
                .toList();
    }

    public ContentBookMarkDTO get(final Long id) {
        return contentBookMarkRepository.findById(id)
                .map(contentBookMark -> mapToDTO(contentBookMark, new ContentBookMarkDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ContentBookMarkDTO contentBookMarkDTO) {
        final ContentBookMark contentBookMark = new ContentBookMark();
        mapToEntity(contentBookMarkDTO, contentBookMark);
        return contentBookMarkRepository.save(contentBookMark).getId();
    }

    public void update(final Long id, final ContentBookMarkDTO contentBookMarkDTO) {
        final ContentBookMark contentBookMark = contentBookMarkRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(contentBookMarkDTO, contentBookMark);
        contentBookMarkRepository.save(contentBookMark);
    }

    public void delete(final Long id) {
        contentBookMarkRepository.deleteById(id);
    }

    private ContentBookMarkDTO mapToDTO(final ContentBookMark contentBookMark,
            final ContentBookMarkDTO contentBookMarkDTO) {
        contentBookMarkDTO.setId(contentBookMark.getId());
        contentBookMarkDTO.setEmail(contentBookMark.getEmail());
        contentBookMarkDTO.setContent(contentBookMark.getContent() == null ? null : contentBookMark.getContent().getId());
        return contentBookMarkDTO;
    }

    private ContentBookMark mapToEntity(final ContentBookMarkDTO contentBookMarkDTO,
            final ContentBookMark contentBookMark) {
        contentBookMark.setEmail(contentBookMarkDTO.getEmail());
        final Content content = contentBookMarkDTO.getContent() == null ? null : contentRepository.findById(contentBookMarkDTO.getContent())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        contentBookMark.setContent(content);
        return contentBookMark;
    }

}
