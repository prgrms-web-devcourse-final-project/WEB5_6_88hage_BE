package com.grepp.funfun.app.domain.content.repository;

import com.grepp.funfun.app.domain.content.entity.QContentImage;
import com.grepp.funfun.app.domain.content.entity.QContentUrl;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.QContent;
import com.grepp.funfun.app.domain.content.entity.QContentCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
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
            String keyword,
            boolean includeExpired,
            Pageable pageable) {

        BooleanBuilder where = new BooleanBuilder()
                .and(categoryEq(categoryParam))
                .and(gunameEq(guname))
                .and(startDateGoe(startDate))
                .and(endDateLoe(endDate))
                .and(keywordEq(keyword));

        if (!includeExpired) {
            where.and(content.endDate.goe(LocalDate.now()));
        }

        OrderSpecifier<?> orderSpecifier = null;
        Sort sort = pageable.getSort();
        log.info("정렬 조건 확인: {}", sort);

        if (sort.isSorted()) {
            String property = sort.iterator().next().getProperty();
            log.info("정렬 필드명: {}", property);
            log.info("북마크 기준 정렬 여부: {}", "bookmarkCount".equals(property));

            if ("bookmarkCount".equals(property)) {
                orderSpecifier = content.bookmarkCount.desc();
                log.info("북마크 DESC OrderSpecifier 생성: {}", orderSpecifier);
            } else if ("endDate".equals(property)) {
                orderSpecifier = content.endDate.asc();
                log.info("마감일 ASC OrderSpecifier 생성: {}", orderSpecifier);
            }
        }
        long total = queryFactory
                .selectFrom(content)
                .join(content.category, category)
                .where(where)
                .fetchCount();

        JPAQuery<Content> query = queryFactory
                .selectFrom(content)
                .join(content.category, category)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (orderSpecifier != null) {
            log.info("OrderSpecifier 적용 전 쿼리");
            query.orderBy(orderSpecifier);
            log.info("OrderSpecifier 적용 완료: {}", orderSpecifier);
        }

        log.info("최종 쿼리 실행 전");
        List<Content> results = query.fetch();
        log.info("쿼리 실행 완료");

        log.info("조회된 결과들:");
        for (int i = 0; i < Math.min(5, results.size()); i++) {
            Content c = results.get(i);
            log.info("{}번째 - ID: {}, bookmarkCount: {}", i+1, c.getId(), c.getBookmarkCount());
        }

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Content> findFilteredContentsByDistance(
            ContentClassification categoryParam,
            String guname,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            double userLat,
            double userLng,
            boolean includeExpired,
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
                        keywordEq(keyword),
                        content.latitude.isNotNull(),
                        content.longitude.isNotNull(),
                        includeExpired ? null : content.endDate.goe(LocalDate.now())
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
                        keywordEq(keyword),
                        content.latitude.isNotNull(),
                        content.longitude.isNotNull(),
                        includeExpired ? null : content.endDate.goe(LocalDate.now())
                )
                .orderBy(distance.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }


    @Override
    public List<Content> findNearby(double latitude, double longitude, double radiusInKm, Long excludeId, int limit, boolean includeExpired) {
        NumberExpression<Double> distance = createDistanceExpression(latitude, longitude);

        BooleanExpression notExpired = includeExpired
                ? null
                : content.endDate.isNull()
                .or(content.endDate.goe(LocalDate.now()));


        return queryFactory
                .selectFrom(content)
                .where(
                        content.id.ne(excludeId),
                        content.latitude.isNotNull(),
                        content.longitude.isNotNull(),
                        distance.lt(radiusInKm),
                        notExpired
                )
                .orderBy(distance.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Content> findByCategoryCategory(ContentClassification categoryParam, boolean includeExpired) {
        BooleanExpression notExpired = includeExpired
                ? null
                : content.endDate.isNull()
                .or(content.endDate.goe(LocalDate.now()));

        return queryFactory
                .selectFrom(content)
                .join(content.category, category).fetchJoin()
                .where(
                        category.category.eq(categoryParam),
                        notExpired
                )
                .fetch();
    }

    @Override
    public List<Content> findContentsByIdsWithAllRelations(List<Long> ids) {
        return queryFactory
            .selectFrom(content)
            // @BatchSize 어노테이션이 LAZY 로딩 시 N+1 문제를 최적화
            .leftJoin(content.category, category).fetchJoin() // ManyToOne인 category만 fetchJoin
            .where(content.id.in(ids))
            .distinct() // Content 엔티티 자체의 중복 제거
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

    private BooleanExpression keywordEq(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        BooleanExpression titleCondition = content.contentTitle.containsIgnoreCase(keyword);
        BooleanExpression addressCondition = content.address.containsIgnoreCase(keyword);
        BooleanExpression gunameCondition = content.guname.containsIgnoreCase(keyword);

        // 카테고리 한글 검색 매핑
        BooleanExpression categoryCondition = getCategoryCondition(keyword);

        BooleanExpression result = titleCondition.or(addressCondition).or(gunameCondition);
        if (categoryCondition != null) {
            result = result.or(categoryCondition);
        }

        return result;
    }

    private BooleanExpression getCategoryCondition(String keyword) {
        String lowerKeyword = keyword.toLowerCase().trim();

        return switch (lowerKeyword) {
            case "연극", "theater", "theatre" ->
                    content.category.category.eq(ContentClassification.THEATER);
            case "무용", "서양무용", "한국무용", "발레", "ballet", "dance" ->
                    content.category.category.eq(ContentClassification.DANCE);

            case "대중무용", "pop dance", "힙합", "hiphop", "현대무용" ->
                    content.category.category.eq(ContentClassification.POP_DANCE);

            case "클래식", "서양음악", "classic", "classical", "오케스트라", "심포니" ->
                    content.category.category.eq(ContentClassification.CLASSIC);

            case "국악", "한국음악", "gukak", "전통음악", "판소리", "가야금" ->
                    content.category.category.eq(ContentClassification.GUKAK);

            case "대중음악", "pop", "팝", "콘서트", "concert", "음악", "music", "k-pop" ->
                    content.category.category.eq(ContentClassification.POP_MUSIC);

            case "복합", "mix", "혼합" ->
                    content.category.category.eq(ContentClassification.MIX);

            case "서커스", "마술", "magic", "circus", "매직" ->
                    content.category.category.eq(ContentClassification.MAGIC);

            case "뮤지컬", "musical" ->
                    content.category.category.eq(ContentClassification.MUSICAL);

            case "관광지", "tour", "여행", "관광", "투어" ->
                    content.category.category.eq(ContentClassification.TOUR);

            case "문화시설", "culture", "박물관", "미술관", "갤러리", "전시관" ->
                    content.category.category.eq(ContentClassification.CULTURE);

            case "레포츠", "스포츠", "sports", "운동", "체육", "레저" ->
                    content.category.category.eq(ContentClassification.SPORTS);

            default -> null;
        };
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
