package com.grepp.funfun.app.model.participant.repository;

import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.entity.QParticipant;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantRepositoryCustomImpl implements ParticipantRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QParticipant qParticipant = QParticipant.participant;

    public ParticipantRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Participant> findPendingMembers(Long groupId) {
        return queryFactory
            .selectFrom(qParticipant)
            .where(qParticipant.group.id.eq(groupId)
                .and(qParticipant.status.eq(ParticipantStatus.PENDING))
                .and(qParticipant.activated.eq(true)))
            .fetch();
    }
}
