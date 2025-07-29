package com.grepp.funfun.app.domain.participant.repository;

import com.grepp.funfun.app.domain.group.entity.QGroup;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.dto.payload.GroupCompletedStatsResponse;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.entity.QParticipant;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.user.entity.QUser;
import com.grepp.funfun.app.domain.user.entity.QUserInfo;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ParticipantRepositoryCustomImpl implements ParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QParticipant qParticipant = QParticipant.participant;
    private final QUser qUser = QUser.user;
    private final QGroup group = QGroup.group;
    private final QUserInfo qUserInfo = QUserInfo.userInfo;

    // 모임 참여 신청 대기자 조회
    @Override
    public List<Participant> findTruePendingMembers(Long groupId) {
        return queryFactory
            .selectFrom(qParticipant)
            .join(qParticipant.user, qUser)
            .where(
                qParticipant.group.id.eq(groupId),
                qParticipant.status.eq(ParticipantStatus.PENDING),
                qParticipant.activated.isTrue(),
                qUser.activated.isTrue()
            )
            .fetch();
    }

    // 모임 승인 참가자 조회(for 채팅)
    @Override
    public List<Participant> findTrueApproveMembers(Long groupId) {
        return queryFactory
            .selectFrom(qParticipant)
            .join(qParticipant.user, qUser)
            .join(qUser.info, qUserInfo)
            .where(
                qParticipant.group.id.eq(groupId),
                qParticipant.status.eq(ParticipantStatus.APPROVED),
                qParticipant.activated.isTrue(),
                qUser.activated.isTrue()
            )
            .fetch();
    }

    @Override
    public Optional<Participant> findKickoutMember(Long groupId, String targetEmail){
        Participant participant = queryFactory
            .selectFrom(qParticipant)
            .where(
                qParticipant.group.id.eq(groupId),
                qParticipant.user.email.eq(targetEmail),
                qParticipant.status.eq(ParticipantStatus.APPROVED),
                qParticipant.activated.eq(true)
            )
            .fetchOne();

        return Optional.ofNullable(participant);
    }

    // 참여자(true 이고 승인 된 사용자)
    @Override
    public Optional<Participant> findTrueMember(Long groupId, String targetEmail) {
        Participant participant = queryFactory
            .selectFrom(qParticipant)
            .join(qParticipant.user, qUser)
            .where(
                qParticipant.group.id.eq(groupId),
                qUser.email.eq(targetEmail),
                qParticipant.status.eq(ParticipantStatus.APPROVED),
                qParticipant.activated.eq(true),
                qUser.activated.eq(true)
            )
            .fetchOne();

        return Optional.ofNullable(participant);
    }


    @Override
    public List<GroupCompletedStatsResponse> findGroupCompletedStats(String email) {
        return queryFactory
            .select(Projections.constructor(GroupCompletedStatsResponse.class,
                group.category,
                group.count()
            ))
            .from(qParticipant)
            .join(qParticipant.group, group)
            .where(
                qParticipant.user.email.eq(email),
                qParticipant.status.eq(ParticipantStatus.GROUP_COMPLETE),
                group.status.eq(GroupStatus.COMPLETED)
            )
            .groupBy(group.category)
            .fetch();
    }

    @Override
    public List<Participant> findDeletableParticipants(String email) {
        return queryFactory
            .selectFrom(qParticipant)
            .where(
                qParticipant.user.email.eq(email),
                qParticipant.role.eq(ParticipantRole.MEMBER),
                qParticipant.status.in(ParticipantStatus.PENDING, ParticipantStatus.APPROVED),
                qParticipant.activated.isTrue()
            )
            .fetch();
    }

}