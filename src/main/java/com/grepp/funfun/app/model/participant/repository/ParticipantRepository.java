package com.grepp.funfun.app.model.participant.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant findFirstByUser(User user);

    Participant findFirstByGroup(Group group);

    Boolean existsByUserAndGroup(User user, Group group);

}
