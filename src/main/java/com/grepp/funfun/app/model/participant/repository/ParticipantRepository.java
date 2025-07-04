package com.grepp.funfun.app.model.participant.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.participant.code.ParticipantRole;
import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ParticipantRepository extends JpaRepository<Participant, Long>,ParticipantRepositoryCustom {

    Participant findFirstByUser(User user);

    Participant findFirstByGroup(Group group);

    Boolean existsByUserAndGroup(User user, Group group);

    Participant findByUserEmail(String email);

    Participant findLeaderEmailByGroupId(Long groupId);

    List<Participant> findByGroupIdAndStatusAndActivated(Long groupId, ParticipantStatus status, Boolean activated);
    Participant findByGroupIdAndUserEmail(Long groupId,String userEmail);
}
