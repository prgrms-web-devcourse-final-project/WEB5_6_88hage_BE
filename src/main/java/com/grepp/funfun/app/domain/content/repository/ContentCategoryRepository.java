package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {

//    Optional<ContentCategory> findByCategory(ContentClassification category);
}
