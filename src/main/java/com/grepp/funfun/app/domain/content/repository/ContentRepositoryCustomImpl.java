package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.QContent;
import com.grepp.funfun.app.domain.content.entity.QContentCategory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ContentRepositoryCustomImpl extends QuerydslRepositorySupport implements ContentRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QContent content = QContent.content;
    private final QContentCategory category = QContentCategory.contentCategory;

    public ContentRepositoryCustomImpl(EntityManager entityManager) {
        super(Content.class);
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Optional<Content> findByIdWithCategory(Long id) {
        Content result = queryFactory
                .selectFrom(content)
                .join(content.category, category).fetchJoin()
                .where(content.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Content> findFilteredContents(
            ContentClassification categoryParam,
            String guname,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        JPAQuery<Content> query = queryFactory
                .selectFrom(content)
                .join(content.category, category)
                .where(
                        categoryEq(categoryParam),
                        gunameEq(guname),
                        startDateGoe(startDate),
                        endDateLoe(endDate)
                );

        long total = query.fetchCount();

        List<Content> results = getQuerydsl()
                .applyPagination(pageable, query)
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Content> findFilteredContentsByDistance(
            ContentClassification categoryParam,
            String guname,
            LocalDate startDate,
            LocalDate endDate,
            double userLat,
            double userLng,
            Pageable pageable) {

        // 거리 계산(사용자의 위치 기준)
        NumberExpression<Double> distance = createDistanceExpression(userLat, userLng);

        long total = queryFactory
                .selectFrom(content)
                .join(content.category, category)
                .where(
                        categoryEq(categoryParam),
                        gunameEq(guname),
                        startDateGoe(startDate),
                        endDateLoe(endDate),
                        content.latitude.isNotNull(),
                        content.longitude.isNotNull()
                )
                .fetchCount();

        List<Content> results = queryFactory
                .selectFrom(content)
                .join(content.category, category)
                .where(
                        categoryEq(categoryParam),
                        gunameEq(guname),
                        startDateGoe(startDate),
                        endDateLoe(endDate),
                        content.latitude.isNotNull(),
                        content.longitude.isNotNull()
                )
                .orderBy(distance.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }


    @Override
    public List<Content> findNearby(double lat, double lng, double radiusInKm, Long excludeId, int limit) {
        NumberExpression<Double> distance = createDistanceExpression(lat, lng);

        return queryFactory
                .selectFrom(content)
                .where(
                        content.id.ne(excludeId),
                        content.latitude.isNotNull(),
                        content.longitude.isNotNull(),
                        distance.lt(radiusInKm)
                )
                .orderBy(distance.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Content> findByCategoryCategory(ContentClassification categoryParam) {
        return queryFactory
                .selectFrom(content)
                .join(content.category, category).fetchJoin()
                .where(category.category.eq(categoryParam))
                .fetch();
    }

    private BooleanExpression categoryEq(ContentClassification categoryParam) {
        return categoryParam != null ? content.category.category.eq(categoryParam) : null;
    }

    private BooleanExpression gunameEq(String guname) {
        return guname != null ? content.guname.eq(guname) : null;
    }

    private BooleanExpression startDateGoe(LocalDate startDate) {
        return startDate != null ? content.startDate.goe(startDate) : null;
    }

    private BooleanExpression endDateLoe(LocalDate endDate) {
        return endDate != null ? content.endDate.loe(endDate) : null;
    }

    private NumberExpression<Double> createDistanceExpression(double baseLat, double baseLng) {
        return Expressions.numberTemplate(Double.class,
                "6371 * acos(" +
                        "cos(radians({0})) * cos(radians({1})) * " +
                        "cos(radians({2}) - radians({3})) + " +
                        "sin(radians({0})) * sin(radians({1}))" +
                        ")",
                baseLat, content.latitude, content.longitude, baseLng);
    }
}
