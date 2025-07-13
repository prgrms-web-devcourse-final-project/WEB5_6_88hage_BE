package com.grepp.funfun.app.domain.participant.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ParticipantRepository extends JpaRepository<Participant, Long>,ParticipantRepositoryCustom {

    Participant findFirstByUser(User user);

    Participant findFirstByGroup(Group group);

    Boolean existsByUserAndGroup(User user, Group group);

    Optional<Participant> findByGroupIdAndUserEmail(Long groupId,String userEmail);

    // 특정 그룹 참여 기록 찾기
    Optional<Participant> findByUserAndGroup(User user, Group group);
    String user(User user);

    long countByUserEmailAndRoleAndStatus(String email, ParticipantRole participantRole, ParticipantStatus participantStatus);
}
