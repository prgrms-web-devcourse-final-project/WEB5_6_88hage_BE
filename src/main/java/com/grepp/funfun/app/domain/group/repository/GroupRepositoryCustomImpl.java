package com.grepp.funfun.app.domain.group.repository;

import static com.grepp.funfun.app.domain.participant.entity.QParticipant.participant;
import static com.grepp.funfun.app.domain.user.entity.QUser.user;

import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.QGroup;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class GroupRepositoryCustomImpl implements GroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QGroup group = QGroup.group;

    public GroupRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

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

    @Override
    public List<Group> findMyGroups(String userEmail) {
        // 1. 내가 참여중인 그룹 ID
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

        // 2. 해당 그룹들의 모든 참가자들 가져오기
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
}
