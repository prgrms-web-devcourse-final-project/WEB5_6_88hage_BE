package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepositoryCustom {

    Optional<Content> findByIdWithCategory(Long id);

    Page<Content> findFilteredContents(
            ContentClassification category,
            String guname,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            boolean includeExpired,
            Pageable pageable);

    Page<Content> findFilteredContentsByDistance(
            ContentClassification category,
            String guname,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            double userLat,
            double userLng,
            boolean includeExpired,
            Pageable pageable
    );

    List<Content> findNearby(double latitude, double longitude, double radiusInKm, Long excludeId, int limit, boolean includeExpired);

    List<Content> findByCategoryCategory(ContentClassification category, boolean includeExpired);

    List<Content> findContentsByIdsWithAllRelations(List<Long> recommendIds);
}
