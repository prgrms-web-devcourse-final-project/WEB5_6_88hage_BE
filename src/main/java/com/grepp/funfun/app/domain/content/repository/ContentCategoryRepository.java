package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {

    Optional<ContentCategory> findByCategory(ContentClassification category);

}

