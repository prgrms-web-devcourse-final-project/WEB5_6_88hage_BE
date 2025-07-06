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
import java.util.stream.Collectors;

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

    public List<ContentBookmarkDTO> getByEmail(String email) {
        return contentBookmarkRepository.findAllByUser_EmailOrderByCreatedAtDesc(email)
                .stream()
                .map(bookmark -> mapToDTO(bookmark, new ContentBookmarkDTO()))
                .collect(Collectors.toList());
    }

    public Long addByEmail(final ContentBookmarkDTO contentBookmarkDTO, String email) {
        if (contentBookmarkRepository.existsByUser_EmailAndContent_Id(email, contentBookmarkDTO.getContentId())) {
            throw new CommonException(ResponseCode.USER_EMAIL_DUPLICATE);
        }
        User user = userRepository.findById(email)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        Content content = contentRepository.findById(contentBookmarkDTO.getContentId())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        final ContentBookmark contentBookmark = new ContentBookmark();
        contentBookmark.setUser(user);
        contentBookmark.setContent(content);
        return contentBookmarkRepository.save(contentBookmark).getId();
    }

    public void deleteByEmail(Long id, String email) {
        ContentBookmark bookmark = contentBookmarkRepository
                .findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        if(!bookmark.getUser().getEmail().equals(email)) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }
        contentBookmarkRepository.delete(bookmark);
    }

    // controller
    public ContentBookmarkDTO get(final Long id) {
        return contentBookmarkRepository.findById(id)
                .map(contentBookmark -> mapToDTO(contentBookmark, new ContentBookmarkDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ContentBookmarkDTO contentBookmarkDTO) {
        final ContentBookmark contentBookmark = new ContentBookmark();
        mapToEntity(contentBookmarkDTO, contentBookmark);
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
