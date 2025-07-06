package com.grepp.funfun.app.model.bookmark.service;

import com.grepp.funfun.app.model.bookmark.dto.ContentBookmarkDTO;
import com.grepp.funfun.app.model.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.model.bookmark.repository.ContentBookmarkRepository;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ContentBookmarkService {

    private final ContentBookmarkRepository contentBookmarkRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

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
        if (contentBookmarkRepository.existsByIdAndUser_Email(contentBookmarkDTO.getId(), contentBookmarkDTO.getEmail())) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
        User user = userRepository.findById(contentBookmarkDTO.getEmail())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        Content content = contentRepository.findById(contentBookmarkDTO.getContentId())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        final ContentBookmark contentBookmark = new ContentBookmark();
        contentBookmark.setUser(user);
        contentBookmark.setContent(content);
        return contentBookmarkRepository.save(contentBookmark).getId();
    }

    public void update(final Long id, final ContentBookmarkDTO contentBookmarkDTO) {
        final ContentBookmark contentBookmark = contentBookmarkRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(contentBookmarkDTO, contentBookmark);
        contentBookmarkRepository.save(contentBookmark);
    }

    public void delete(Long id) {
        ContentBookmark bookmark = contentBookmarkRepository
                .findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        contentBookmarkRepository.delete(bookmark);
    }

    private ContentBookmarkDTO mapToDTO(final ContentBookmark contentBookmark,
            final ContentBookmarkDTO contentBookmarkDTO) {
        contentBookmarkDTO.setId(contentBookmark.getId());
        contentBookmarkDTO.setEmail(contentBookmark.getUser() != null ? contentBookmark.getUser().getEmail() : null);
        contentBookmarkDTO.setContentId(contentBookmark.getContent() == null ? null : contentBookmark.getContent().getId());
        return contentBookmarkDTO;
    }

    private ContentBookmark mapToEntity(final ContentBookmarkDTO contentBookmarkDTO,
            final ContentBookmark contentBookmark) {
        User user = userRepository.findById(contentBookmarkDTO.getEmail())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        final Content content = contentBookmarkDTO.getContentId() == null ? null : contentRepository.findById(contentBookmarkDTO.getContentId())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        contentBookmark.setContent(content);
        return contentBookmark;
    }

}
