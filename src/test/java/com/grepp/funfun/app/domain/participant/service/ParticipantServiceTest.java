package com.grepp.funfun.app.domain.participant.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private CalendarService calendarService;

    // InjectMocks
    // 테스트 대상인 실제 Service 객체를 생성
    @InjectMocks
    private ParticipantService participantService;

    @Test
    public void approveParticipant(){
        // GIVEN
        Long groupId = 1L;
        String leaderEmail = "test@aaa.aaa";
        String pendingEmail = "testPending@aaa.aaa";

        // 로그인
        User mockLeader = new User();
        mockLeader.setEmail(leaderEmail);

        User mockPending = new User();
        mockPending.setEmail(pendingEmail);

        Group mockGroup = new Group();
        mockGroup.setLeader(mockLeader);
        mockGroup.setId(groupId);
        mockGroup.setMaxPeople(5);
        mockGroup.setNowPeople(1);
        mockGroup.setStatus(GroupStatus.RECRUITING);

        Participant mockParticipant = new Participant();
        mockParticipant.setUser(mockPending);
        mockParticipant.setGroup(mockGroup);
        mockParticipant.setStatus(ParticipantStatus.PENDING);

        // WHEN
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(mockGroup));
        when(participantRepository.findByGroupIdAndUserEmail(groupId, pendingEmail))
            .thenReturn(Optional.of(mockParticipant));
        when(groupRepository.save(any(Group.class))).thenReturn(mockGroup);

        participantService.approveParticipant(groupId, List.of(pendingEmail), leaderEmail);

        // THEN
        verify(groupRepository).findById(groupId);
        verify(participantRepository).findByGroupIdAndUserEmail(groupId, pendingEmail);

        // 사용자 승인 + 현재 모임 인원 +1 증가 확인
        assertThat(mockParticipant.getStatus()).isEqualTo(ParticipantStatus.APPROVED);
        assertThat(mockGroup.getNowPeople()).isEqualTo(2);
    }

}
