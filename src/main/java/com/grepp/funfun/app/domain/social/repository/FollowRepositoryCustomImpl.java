package com.grepp.funfun.app.domain.social.repository;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import com.grepp.funfun.app.domain.social.entity.QFollow;
import com.grepp.funfun.app.domain.user.entity.QUser;
import com.grepp.funfun.app.domain.user.entity.QUserInfo;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QFollow follow = QFollow.follow;
    private final QFollow followSub = new QFollow("followSub");
    private final QUser user = QUser.user;
    private final QUserInfo userInfo = QUserInfo.userInfo;

    @Override
    public Page<FollowsResponse> findFollowersByFolloweeEmail(String followeeEmail,
        Pageable pageable) {
        List<FollowsResponse> content = queryFactory
            .select(Projections.constructor(
                FollowsResponse.class,
                user.email,
                user.nickname,
                userInfo.imageUrl,
                JPAExpressions
                    .selectOne()
                    .from(followSub)
                    .where(
                        followSub.follower.email.eq(followeeEmail)
                            .and(followSub.followee.email.eq(user.email))
                    )
                    .exists()
            ))
            .from(follow)
            .join(follow.follower, user)
            .join(user.info, userInfo)
            .where(follow.followee.email.eq(followeeEmail))
            .orderBy(getOrderSpecifiers(pageable.getSort()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(follow.count())
            .from(follow)
            .where(follow.followee.email.eq(followeeEmail));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<FollowsResponse> findFollowingsByFollowerEmail(String followerEmail,
        Pageable pageable) {
        List<FollowsResponse> content = queryFactory
            .select(Projections.constructor(
                FollowsResponse.class,
                user.email,
                user.nickname,
                userInfo.imageUrl,
                JPAExpressions
                    .selectOne()
                    .from(followSub)
                    .where(
                        followSub.follower.email.eq(user.email)
                            .and(followSub.followee.email.eq(followerEmail))
                    )
                    .exists()
            ))
            .from(follow)
            .join(follow.followee, user)
            .join(user.info, userInfo)
            .where(follow.follower.email.eq(followerEmail))
            .orderBy(getOrderSpecifiers(pageable.getSort()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(follow.count())
            .from(follow)
            .where(follow.follower.email.eq(followerEmail));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        return sort.stream()
            .map(order -> {
                String property = order.getProperty();
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                return switch (property) {
                    case "createdAt" -> new OrderSpecifier<>(direction, follow.createdAt);
                    case "nickname" -> new OrderSpecifier<>(direction, user.nickname);
                    default -> new OrderSpecifier<>(Order.ASC, user.nickname);
                };
            }).toArray(OrderSpecifier[]::new);
    }
}
