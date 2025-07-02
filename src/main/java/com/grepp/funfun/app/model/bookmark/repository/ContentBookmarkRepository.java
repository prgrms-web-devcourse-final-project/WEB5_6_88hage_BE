package com.grepp.funfun.app.model.bookmark.repository;

import com.grepp.funfun.app.model.bookmark.entity.ContentBookmark;
import com.grepp.funfun.app.model.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentBookmarkRepository extends JpaRepository<ContentBookmark, Long> {

    ContentBookmark findFirstByContent(Content content);

}
