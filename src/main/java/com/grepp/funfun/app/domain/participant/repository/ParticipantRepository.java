package com.grepp.funfun.app.domain.participant.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ParticipantRepository extends JpaRepository<Participant, Long>,ParticipantRepositoryCustom {

    Participant findFirstByUser(User user);

    Participant findFirstByGroup(Group group);

    Boolean existsByUserAndGroup(User user, Group group);

    Optional<Participant> findByGroupIdAndUserEmail(Long groupId,String userEmail);

    String user(User user);
}
