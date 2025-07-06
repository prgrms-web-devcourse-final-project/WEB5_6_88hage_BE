package com.grepp.funfun.app.model.content.repository;

import com.grepp.funfun.app.model.content.code.ContentClassification;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {

//    Optional<ContentCategory> findByCategory(ContentClassification category);
}
