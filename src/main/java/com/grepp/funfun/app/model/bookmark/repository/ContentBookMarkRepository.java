package com.grepp.funfun.app.model.bookmark.repository;

import com.grepp.funfun.app.model.bookmark.entity.ContentBookMark;
import com.grepp.funfun.app.model.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentBookMarkRepository extends JpaRepository<ContentBookMark, Long> {

    ContentBookMark findFirstByContent(Content content);

}
