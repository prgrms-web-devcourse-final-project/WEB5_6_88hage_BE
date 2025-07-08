package com.grepp.funfun.app.model.participant.repository;

import com.grepp.funfun.app.model.participant.entity.Participant;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepositoryCustom {

    List<Participant> findTruePendingMembers(Long groupId);
    List<Participant> findTrueApproveMembers(Long groupId);
    Optional<Participant> findTrueMember(Long groupId, String targetEmail);
    Optional<Participant> findKickoutMember(Long groupId, String targetEmail);
}
