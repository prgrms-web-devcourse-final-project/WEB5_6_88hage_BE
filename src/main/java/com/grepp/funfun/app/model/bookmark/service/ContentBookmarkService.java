package com.grepp.funfun.app.model.bookmark.service;

import com.grepp.funfun.app.model.bookmark.dto.ContentBookmarkDTO;
import com.grepp.funfun.app.model.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.model.bookmark.repository.ContentBookmarkRepository;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ContentBookmarkService {

    private final ContentBookmarkRepository contentBookmarkRepository;
    private final ContentRepository contentRepository;

    public ContentBookmarkService(final ContentBookmarkRepository contentBookmarkRepository,
            final ContentRepository contentRepository) {
        this.contentBookmarkRepository = contentBookmarkRepository;
        this.contentRepository = contentRepository;
    }

    public List<ContentBookmarkDTO> findAll() {
        final List<ContentBookmark> contentBookmarks = contentBookmarkRepository.findAll(Sort.by("id"));
        return contentBookmarks.stream()
                .map(contentBookmark -> mapToDTO(contentBookmark, new ContentBookmarkDTO()))
                .toList();
    }

    public ContentBookmarkDTO get(final Long id) {
        return contentBookmarkRepository.findById(id)
                .map(contentBookmark -> mapToDTO(contentBookmark, new ContentBookmarkDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long add(final ContentBookmarkDTO contentBookmarkDTO) {
        if (contentBookmarkRepository.existsByIdAndEmail(contentBookmarkDTO.getId(), contentBookmarkDTO.getEmail())) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
        final ContentBookmark contentBookmark = new ContentBookmark();
        mapToEntity(contentBookmarkDTO, contentBookmark);
        return contentBookmarkRepository.save(contentBookmark).getId();
    }

//    public void update(final Long id, final ContentBookmarkDTO contentBookmarkDTO) {
//        final ContentBookmark contentBookmark = contentBookmarkRepository.findById(id)
//                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
//        mapToEntity(contentBookmarkDTO, contentBookmark);
//        contentBookmarkRepository.save(contentBookmark);
//    }

    public void delete(Long id, String email) {
        ContentBookmark bookmark = contentBookmarkRepository
                .findByIdAndEmail(id, email)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        contentBookmarkRepository.delete(bookmark);
    }

    private ContentBookmarkDTO mapToDTO(final ContentBookmark contentBookmark,
            final ContentBookmarkDTO contentBookmarkDTO) {
        contentBookmarkDTO.setId(contentBookmark.getId());
        contentBookmarkDTO.setEmail(contentBookmark.getEmail());
        contentBookmarkDTO.setContent(contentBookmark.getContent() == null ? null : contentBookmark.getContent().getId());
        return contentBookmarkDTO;
    }

    private ContentBookmark mapToEntity(final ContentBookmarkDTO contentBookmarkDTO,
            final ContentBookmark contentBookmark) {
        contentBookmark.setEmail(contentBookmarkDTO.getEmail());
        final Content content = contentBookmarkDTO.getContent() == null ? null : contentRepository.findById(contentBookmarkDTO.getContent())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        contentBookmark.setContent(content);
        return contentBookmark;
    }

}
