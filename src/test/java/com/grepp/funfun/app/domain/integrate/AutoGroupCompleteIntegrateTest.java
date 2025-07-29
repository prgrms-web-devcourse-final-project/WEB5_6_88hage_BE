package com.grepp.funfun.app.domain.integrate;

import static org.assertj.core.api.Assertions.assertThat;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.group.scheduler.GroupCompleteScheduler;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RequiredArgsConstructor
@Slf4j
public class AutoGroupCompleteIntegrateTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private GroupCompleteScheduler scheduler;

    private Long testGroupId;


    @BeforeEach
    public void init(){

        User leader = User.builder()
            .email("leader@aaa.aaa")
            .build();

        userRepository.save(leader);

        Group group = Group.builder()
            .title("모임0728테스트")
            .leader(leader)
            .status(GroupStatus.RECRUITING)
            .groupDate(LocalDateTime.of(2025, 7, 27, 18, 0))
            .nowPeople(1)
            .maxPeople(2)
            .during(1)
            .build();

        Group saveGroup = groupRepository.save(group);
        this.testGroupId = saveGroup.getId();

        User user = User.builder()
            .email("user@aaa.aaa")
            .build();

        userRepository.save(user);

        Participant pLeader = Participant.builder()
            .group(saveGroup)
            .user(leader)
            .role(ParticipantRole.LEADER)
            .status(ParticipantStatus.APPROVED)
            .build();

        Participant pUser = Participant.builder()
            .group(saveGroup)
            .user(user)
            .role(ParticipantRole.MEMBER)
            .status(ParticipantStatus.APPROVED)
            .build();

        participantRepository.save(pLeader);
        participantRepository.save(pUser);

    }

    @Test
    @Transactional
    public void autoGroupCompleteIntegrateTest(){
        // 스케줄러를 통해 자동적으로 변경
        scheduler.groupComplete();

        Group group = groupRepository.findById(testGroupId).orElseThrow();

        Participant leader = group.getParticipants().stream()
            .filter(p -> p.getRole() == ParticipantRole.LEADER)
            .findFirst()
            .orElseThrow();

        assertThat(group.getActivated()).isFalse();
        assertThat(group.getStatus()).isEqualTo(GroupStatus.COMPLETED);

        assertThat(leader.getStatus()).isEqualTo(ParticipantStatus.GROUP_COMPLETE);
    }
}
