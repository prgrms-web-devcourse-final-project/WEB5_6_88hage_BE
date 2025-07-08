package com.grepp.funfun.app.model.content.repository;

import com.grepp.funfun.app.model.content.code.ContentClassification;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ContentRepository extends JpaRepository<Content, Long> {

    Content findFirstByCategory(ContentCategory contentCategory);

    @Query("SELECT c FROM Content c JOIN FETCH c.category WHERE c.id = :id")
    Optional<Content> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT c FROM Content c " +
            "WHERE (:category IS NULL OR c.category.category = :category) " +
            "AND (:guName IS NULL OR c.guName = :guName) " +
            "AND (:startDate IS NULL OR c.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR c.endDate <= :endDate)")
    Page<Content> findFilteredContents(
            @Param("category") ContentClassification category,
            @Param("guName") String guName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query(value = """
    SELECT * FROM content c
    WHERE c.id != :excludeId 
    AND c.latitude IS NOT NULL 
    AND c.longitude IS NOT NULL
    AND (
        6371 * acos(
            cos(radians(:lat)) * cos(radians(c.latitude)) *
            cos(radians(c.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(c.latitude))
        )
    ) < :radiusInKm
    ORDER BY (
        6371 * acos(
            cos(radians(:lat)) * cos(radians(c.latitude)) *
            cos(radians(c.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(c.latitude))
        )
    ) ASC
    LIMIT :limit
    """, nativeQuery = true)
    List<Content> findNearby(@Param("lat") double lat,
                             @Param("lng") double lng,
                             @Param("radiusInKm") double radiusInKm,
                             @Param("excludeId") Long excludeId,
                             @Param("limit") int limit);

    @Query("SELECT c FROM Content c JOIN FETCH c.category WHERE c.category.category = :category")
    List<Content> findByCategoryCategory(@Param("category") ContentClassification category);

//    List<Content> findAllByOrderByBookmarkCountDesc();
}
