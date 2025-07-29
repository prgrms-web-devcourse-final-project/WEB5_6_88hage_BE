package com.grepp.funfun.app.domain.participant.repository;

import com.grepp.funfun.app.domain.participant.dto.payload.GroupCompletedStatsResponse;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepositoryCustom {

    List<Participant> findTruePendingMembers(Long groupId);
    List<Participant> findTrueApproveMembers(Long groupId);
    Optional<Participant> findTrueMember(Long groupId, String targetEmail);
    Optional<Participant> findKickoutMember(Long groupId, String targetEmail);
    List<GroupCompletedStatsResponse> findGroupCompletedStats(String email);

    // 회원 탈퇴 시 삭제 가능한 참여 중인 모임 조회
    List<Participant> findDeletableParticipants(String email);
}
