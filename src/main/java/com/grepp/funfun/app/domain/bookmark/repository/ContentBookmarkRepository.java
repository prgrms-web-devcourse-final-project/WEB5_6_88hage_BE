package com.grepp.funfun.app.domain.bookmark.repository;

import com.grepp.funfun.app.domain.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ContentBookmarkRepository extends JpaRepository<ContentBookmark, Long> {

    ContentBookmark findFirstByContent(Content content);

    boolean existsByUser_EmailAndContent_Id(String email, Long contentId);

    List<ContentBookmark> findAllByUser_EmailOrderByCreatedAtDesc(String email);

    long countByContent_Id(Long contentId);

}
