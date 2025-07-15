package com.grepp.funfun.app.domain.group.repository;

import static com.grepp.funfun.app.domain.group.entity.QGroupHashtag.groupHashtag;
import static com.grepp.funfun.app.domain.participant.entity.QParticipant.participant;
import static com.grepp.funfun.app.domain.user.entity.QUser.user;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.QGroup;
import com.grepp.funfun.app.domain.group.entity.QGroupHashtag;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.user.entity.QUser;
import com.grepp.funfun.app.domain.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryCustomImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QGroup group = QGroup.group;
    private final QUser user = QUser.user;
    QGroupHashtag hashtag = groupHashtag;

    // 모집중인 모임 조회
    @Override
    public Optional<Group> findActiveRecruitingGroup(Long groupId) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(group)
                .where(
                    group.id.eq(groupId)
                        .and(group.activated.eq(true))
                        .and(group.status.eq(GroupStatus.RECRUITING))
                )
                .fetchOne()
        );
    }

    // 내가 참여 중인 모임 조회(채팅)
    @Override
    public List<Group> findMyGroups(String userEmail) {
        // 내가 참여중인 그룹 ID
        List<Long> myGroupIds = queryFactory
            .select(participant.group.id)
            .from(participant)
            .join(participant.user, user)
            .where(
                user.email.eq(userEmail)
                    .and(participant.status.eq(ParticipantStatus.APPROVED))
                    .and(user.activated.eq(true))
            )
            .distinct()
            .fetch();

        // 해당 그룹들의 모든 참가자들 가져오기
        return queryFactory
            .selectFrom(group)
            .join(group.participants, participant).fetchJoin()
            .join(participant.user, user).fetchJoin()
            .where(
                group.id.in(myGroupIds)
                    .and(group.activated.eq(true))
                    .and(participant.status.eq(ParticipantStatus.APPROVED))
                    .and(user.activated.eq(true))
            )
            .distinct()
            .fetch();
    }


    // 모임 상세 조회
    @Override
    public Optional<Group> findByIdWithFullInfo(Long groupId) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(group)
                .join(group.hashtags, hashtag).fetchJoin()
                .where(
                    group.id.eq(groupId)
                        .and(group.activated.eq(true))
                )
                .fetchOne()
        );

    }

    @Override
    public Page<Group> findGroups(
        String category,
        String keyword,
        String sortBy,
        String userEmail,
        Pageable pageable
    ) {
        // 키워드 조건 구성
        BooleanExpression keywordCondition = null;
        if (keyword != null) {
            keywordCondition = group.title.containsIgnoreCase(keyword)
                .or(group.simpleExplain.containsIgnoreCase(keyword))
                .or(JPAExpressions.selectOne()
                    .from(groupHashtag)
                    .where(groupHashtag.group.eq(group)
                        .and(groupHashtag.tag.containsIgnoreCase(keyword)))
                    .exists());
        }

        JPAQuery<Group> baseQuery = queryFactory
            .selectFrom(group)
            .where(
                group.activated.eq(true),
                category != null ? group.category.eq(GroupClassification.valueOf(category)) : null,
                keywordCondition
            );

        String sort = (sortBy != null) ? sortBy : "distance";

        // 정렬 적용
        JPAQuery<Group> orderedQuery = switch (sort) {
            case "recent" -> baseQuery.orderBy(group.createdAt.desc());
            case "viewCount" -> baseQuery.orderBy(group.viewCount.desc());
            default -> {
                if (userEmail != null) {
                    User currentUser = queryFactory
                        .selectFrom(user)
                        .where(user.email.eq(userEmail))
                        .fetchOne();

                    if (currentUser != null &&
                        currentUser.getLatitude() != null &&
                        currentUser.getLongitude() != null) {

                        NumberExpression<Double> distance = Expressions.numberTemplate(Double.class,
                            "6371 * acos(" +
                                "LEAST(1.0, GREATEST(-1.0, " +
                                "cos(radians({0})) * cos(radians({1})) * " +
                                "cos(radians({2}) - radians({3})) + " +
                                "sin(radians({0})) * sin(radians({1}))" +
                                "))" +
                                ")",
                            currentUser.getLatitude(), group.latitude,
                            currentUser.getLongitude(), group.longitude);

                        yield baseQuery
                            .where(group.latitude.isNotNull(), group.longitude.isNotNull())
                            .orderBy(distance.asc());
                    }
                }
                yield baseQuery.orderBy(group.createdAt.desc());
            }
        };

        // 페이징된 컨텐츠 조회
        List<Group> content = orderedQuery
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
            .select(group.count())
            .from(group)
            .where(
                group.activated.eq(true),
                category != null ? group.category.eq(GroupClassification.valueOf(category)) : null,
                keywordCondition
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
