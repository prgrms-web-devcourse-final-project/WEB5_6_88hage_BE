package com.grepp.funfun.app.domain.bookmark.service;

import com.grepp.funfun.app.domain.bookmark.dto.ContentBookmarkDTO;
import com.grepp.funfun.app.domain.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.domain.bookmark.repository.ContentBookmarkRepository;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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
        content.setBookmarkCount(content.getBookmarkCount() + 1);
        contentRepository.save(content);

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

        Content content = bookmark.getContent();
        content.setBookmarkCount(Math.max(0, content.getBookmarkCount() - 1));
        contentRepository.save(content);

        contentBookmarkRepository.delete(bookmark);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void syncBookmarkCounts() {
        List<Content> contents = contentRepository.findAll();
        for (Content content : contents) {
            int actual = (int) contentBookmarkRepository.countByContent_Id(content.getId());
            content.setBookmarkCount(actual);
        }
        contentRepository.saveAll(contents);
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
