package com.grepp.funfun.app.domain.group.repository;

import static com.grepp.funfun.app.domain.group.entity.QGroupHashtag.groupHashtag;
import static com.grepp.funfun.app.domain.participant.entity.QParticipant.participant;

import com.grepp.funfun.app.domain.chat.entity.QGroupChatRoom;
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
    private final QUser leader = new QUser("leader");
    private final QGroupChatRoom groupChatRoom = QGroupChatRoom.groupChatRoom;
    
    QGroupHashtag hashtag = QGroupHashtag.groupHashtag;

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
        return queryFactory
            .selectFrom(group)
            .join(groupChatRoom).on(groupChatRoom.groupId.eq(group.id)).fetchJoin()
            .join(group.participants, participant)
            .join(participant.user, user)
            .where(
                user.email.eq(userEmail)
                    .and(participant.status.eq(ParticipantStatus.APPROVED))
                    .and(group.activated.eq(true))
            )
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

    // 키워드 조회(for 가독성)
    private BooleanExpression keywordFilter(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;

        return group.title.containsIgnoreCase(keyword)
            .or(group.simpleExplain.containsIgnoreCase(keyword))
            .or(JPAExpressions.selectOne()
                .from(groupHashtag)
                .where(groupHashtag.group.eq(group)
                    .and(groupHashtag.tag.containsIgnoreCase(keyword)))
                .exists());
    }

    // 거리순 조회(for 가독성)
    private JPAQuery<Group> distanceSort(JPAQuery<Group> query, String userEmail) {
        if (userEmail == null) return null;

        User currentUser = queryFactory
            .selectFrom(user)
            .where(user.email.eq(userEmail))
            .fetchOne();

        if (currentUser == null || currentUser.getLatitude() == null || currentUser.getLongitude() == null) {
            return null;
        }

        NumberExpression<Double> distance = Expressions.numberTemplate(Double.class,
            "6371 * acos(" +
                "LEAST(1.0, GREATEST(-1.0, " +
                "cos(radians({0})) * cos(radians({1})) * " +
                "cos(radians({2}) - radians({3})) + " +
                "sin(radians({0})) * sin(radians({1})))))",
            currentUser.getLatitude(), group.latitude,
            currentUser.getLongitude(), group.longitude);

        return query
            .where(group.latitude.isNotNull(), group.longitude.isNotNull())
            .orderBy(distance.asc());
    }

    private JPAQuery<Group> applySort(JPAQuery<Group> query, String sortBy, String userEmail) {
        return switch (sortBy != null ? sortBy : "distance") {
            case "recent" -> query.orderBy(group.createdAt.desc());
            case "viewCount" -> query.orderBy(group.viewCount.desc());
            default -> {
                JPAQuery<Group> distanceQuery = distanceSort(query, userEmail);
                yield (distanceQuery != null) ? distanceQuery : query.orderBy(group.createdAt.desc());
            }
        };
    }

    // 모임 조회(거리순,최신순,키워드,조회순)
    @Override
    public Page<Group> findGroups(
        String category,
        String keyword,
        String sortBy,
        String userEmail,
        Pageable pageable
    ) {
        BooleanExpression keywordCondition = keywordFilter(keyword);
        BooleanExpression categoryCondition = (category != null)
            ? group.category.eq(GroupClassification.valueOf(category))
            : null;

        JPAQuery<Group> baseQuery = queryFactory
            .selectFrom(group)
            .where(group.activated.eq(true), categoryCondition, keywordCondition);

        // 정렬 적용
        JPAQuery<Group> orderedQuery = applySort(baseQuery, sortBy, userEmail);

        // 페이징 처리
        List<Group> content = orderedQuery
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
            .select(group.count())
            .from(group)
            .where(group.activated.eq(true), categoryCondition, keywordCondition);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Group> findGroupsByIdsWithAllRelations(List<Long> ids) {
        return queryFactory
            .selectFrom(group)
            .leftJoin(group.leader, leader).fetchJoin()
            .where(group.id.in(ids)) // 주어진 ID 목록으로 필터링
            .distinct()
            .fetch();
    }

}
