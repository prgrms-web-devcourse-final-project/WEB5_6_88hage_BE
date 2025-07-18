package com.grepp.funfun.app.domain.social.repository;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import com.grepp.funfun.app.domain.social.entity.QFollow;
import com.grepp.funfun.app.domain.user.entity.QUser;
import com.grepp.funfun.app.domain.user.entity.QUserInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowRepositoryCustomImpl implements FollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QFollow follow = QFollow.follow;
    private final QUser user = QUser.user;
    private final QUserInfo userInfo = QUserInfo.userInfo;

    @Override
    public List<FollowsResponse> findFollowersByFolloweeEmail(String followeeEmail) {
        return queryFactory
            .select(Projections.constructor(
                FollowsResponse.class,
                user.email,
                user.nickname,
                userInfo.imageUrl
            ))
            .from(follow)
            .join(follow.follower, user)
            .join(user.info, userInfo)
            .where(follow.followee.email.eq(followeeEmail))
            .fetch();
    }

    @Override
    public List<FollowsResponse> findFollowingsByFollowerEmail(String followerEmail) {
        return queryFactory
            .select(Projections.constructor(
                FollowsResponse.class,
                user.email,
                user.nickname,
                userInfo.imageUrl
            ))
            .from(follow)
            .join(follow.followee, user)
            .join(user.info, userInfo)
            .where(follow.follower.email.eq(followerEmail))
            .fetch();
    }
}
