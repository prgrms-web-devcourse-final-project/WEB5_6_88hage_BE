package com.grepp.funfun.app.model.bookmark.repository;

import com.grepp.funfun.app.model.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.model.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ContentBookmarkRepository extends JpaRepository<ContentBookmark, Long> {

    ContentBookmark findFirstByContent(Content content);

    boolean existsByIdAndUser_Email(Long id, String email);

//    Optional<ContentBookmark> findByIdAndEmail(Long id, String email);

}
