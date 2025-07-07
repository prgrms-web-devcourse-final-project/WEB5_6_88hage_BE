package com.grepp.funfun.app.model.participant.repository;

import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.entity.QParticipant;
import com.grepp.funfun.app.model.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ParticipantRepositoryCustomImpl implements ParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QParticipant qParticipant = QParticipant.participant;
    private final QUser qUser = QUser.user;

    public ParticipantRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

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
        log.info("=== findKickoutMember 호출 ===");
        log.info("groupId: {}", groupId);
        log.info("targetEmail: {}", targetEmail);

        // 전체 조건 없이 먼저 확인
        List<Participant> allParticipants = queryFactory
            .selectFrom(qParticipant)
            .where(qParticipant.group.id.eq(groupId))
            .fetch();

        log.info("해당 그룹의 전체 참가자 수: {}", allParticipants.size());
        for(Participant p : allParticipants) {
            log.info("참가자 - 이메일: {}, 상태: {}, 활성화: {}",
                p.getUser().getEmail(), p.getStatus(), p.getActivated());
        }

        Participant participant = queryFactory
            .selectFrom(qParticipant)
            .where(
                qParticipant.group.id.eq(groupId),
                qParticipant.user.email.eq(targetEmail),
                qParticipant.status.eq(ParticipantStatus.APPROVED),
                qParticipant.activated.eq(true)
            )
            .fetchOne();

        log.info("찾은 참가자: {}", participant != null ? participant.getId() : "null");
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
}