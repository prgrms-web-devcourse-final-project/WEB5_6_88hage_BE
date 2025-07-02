package com.grepp.funfun.app.model.content.repository;

import com.grepp.funfun.app.model.content.entity.ContentCategory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {
}
