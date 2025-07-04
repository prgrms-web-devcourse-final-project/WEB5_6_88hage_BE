package com.grepp.funfun.app.model.participant.repository;

import com.grepp.funfun.app.model.participant.entity.Participant;
import java.util.List;

public interface ParticipantRepositoryCustom {

    List<Participant> findPendingMembers(Long groupId);
}
