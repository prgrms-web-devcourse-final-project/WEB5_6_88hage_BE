package com.grepp.funfun.app.model.content.repository;

import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentRepository extends JpaRepository<Content, Long> {

    Content findFirstByCategory(ContentCategory contentCategory);

}
