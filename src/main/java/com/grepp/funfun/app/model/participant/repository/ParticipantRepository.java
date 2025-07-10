package com.grepp.funfun.app.model.participant.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.participant.code.ParticipantRole;
import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ParticipantRepository extends JpaRepository<Participant, Long>,ParticipantRepositoryCustom {

    Participant findFirstByUser(User user);

    Participant findFirstByGroup(Group group);

    Optional<Participant> findByGroupIdAndUserEmail(Long groupId,String userEmail);

    // 특정 그룹 참여 기록 찾기
    Optional<Participant> findByUserAndGroup(User user, Group group);
}
