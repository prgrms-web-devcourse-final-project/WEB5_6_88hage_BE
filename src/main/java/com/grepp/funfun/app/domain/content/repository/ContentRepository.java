package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ContentRepository extends JpaRepository<Content, Long>, ContentRepositoryCustom {

    Optional<Content> findByExternalId(String externalId);

    @Query("SELECT c from Content c JOIN fetch c.category")
    List<Content> findAllWithCategory();
}
