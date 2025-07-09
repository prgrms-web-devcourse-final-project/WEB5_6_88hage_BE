package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepositoryCustom {

    Optional<Content> findByIdWithCategory(Long id);

    Page<Content> findFilteredContents(
            ContentClassification category,
            String guName,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    List<Content> findNearby(double lat, double lng, double radiusInKm, Long excludeId, int limit);

    List<Content> findByCategoryCategory(ContentClassification category);
}
