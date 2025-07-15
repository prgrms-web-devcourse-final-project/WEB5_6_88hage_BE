package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface ContentRepository extends JpaRepository<Content, Long>, ContentRepositoryCustom {

    Content findFirstByCategory(ContentCategory contentCategory);

    @Query("SELECT c from Content c JOIN fetch c.category")
    List<Content> findAllWithCategory();
}
