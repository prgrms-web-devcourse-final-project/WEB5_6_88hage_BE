package com.grepp.funfun.app.domain.group.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.group.dto.payload.GroupRequest;
import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupHashtagRepository;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    // Mock
    // DB 연결 없이 메모리 내에서 동작 -> 이러한 이유로 실제 repo 가 아니라 Mock 으로 가짜 repo 생성
    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupChatRoomRepository groupChatRoomRepository;

    @Mock
    private GroupHashtagRepository groupHashtagRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private CalendarService calendarService;
    // InjectMocks
    // 테스트 대상인 실제 Service 객체를 생성
    @InjectMocks
    private GroupService groupService;


    @Test
    public void createGroupTest() {
        // Given: 테스트에 필요한 상황이나 데이터 준비
        String leaderEmail = "test@example.com";

        User mockUser = new User();
            mockUser.setEmail(leaderEmail);

        GroupRequest requestDto = new GroupRequest();
        requestDto.setTitle("개발 스터디");
        requestDto.setExplain("Spring Boot 공부하는 모임입니다.");
        requestDto.setPlaceName("강남역 1번 출구");
        requestDto.setGroupDate(LocalDateTime.now().plusDays(1));
        requestDto.setAddress("서울 강남구 테헤란로 123");
        requestDto.setCategory(GroupClassification.STUDY);
        requestDto.setHashTags(List.of("자바 스프링 백엔드"));
        requestDto.setDuring(2);

        Group mockSavedGroup = Group.builder()
            .id(1L)
            .title("개발 스터디")
            .build();

        // When: 실제로 테스트할 메서드(기능) 실행
        // 가짜(Mock) 객체의 행동을 정의
        when(userRepository.findByEmail(leaderEmail)).thenReturn(mockUser);
        when(groupRepository.save(any(Group.class))).thenReturn(mockSavedGroup);

        groupService.create(leaderEmail, requestDto);

        // Then: 기대하는 결과나 동작을 검증
        // Mock 객체의 특정 메서드 호출 여부 검증
        verify(userRepository).findByEmail(leaderEmail);
        verify(groupRepository).save(any(Group.class));
        verify(participantRepository).save(any(Participant.class));
        verify(groupHashtagRepository).saveAll(anyList());
        verify(groupChatRoomRepository).save(any(GroupChatRoom.class));

        // assertThat : 잘 생성되었는지 값으로 확인
        assertThat(mockSavedGroup.getTitle()).isEqualTo("개발 스터디");
    }

    @Test
    public void updateGroupTest(){
        // GIVEN
        String leaderEmail = "test@example.com";
        String fakeLeaderEmail = "fake@aaa.aaa";

        // 진짜 리더 유저 세팅
        User realLeader = new User();
        realLeader.setEmail(leaderEmail);

        // 가짜 리더 유저 세팅
        User fakeLeader = new User();
        fakeLeader.setEmail(fakeLeaderEmail);

        Group group = new Group();
        group.setId(1L);
        group.setLeader(realLeader);

        GroupRequest updateDto = new GroupRequest();
        updateDto.setTitle("~~~~~할 사람");

        // WHEN & THEN
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        CommonException exception = assertThrows(CommonException.class, () -> {
            groupService.update(1L, fakeLeaderEmail, updateDto);
        });
        // assertThat : 결과 확인 ex) 권한이 없습니다가 출력되는지 확인
        // 결과 : 모임의 leader 가 아니기에 권한이 없음
        assertThat(exception.getMessage()).contains("권한이 없습니다");
    }

    @Test
    public void deleteGroupTest(){
        //GIVEN
        String leaderEmail = "test@example.com";

        User mockUser = new User();
        mockUser.setEmail(leaderEmail);
        mockUser.setStatus(UserStatus.ACTIVE);

        Group mockGroup = new Group();
        mockGroup.setId(1L);
        mockGroup.setLeader(mockUser);

        Participant participant = new Participant();
        participant.setGroup(mockGroup);
        participant.setStatus(ParticipantStatus.APPROVED);
        participant.setRole(ParticipantRole.MEMBER);
        participant.setUser(mockUser);

        mockGroup.setParticipants(List.of(participant));

        //WHEN
        when(groupRepository.findById(1L)).thenReturn(Optional.of(mockGroup));

        groupService.delete(1L, leaderEmail);

        //THEN (결과 : 참여자 상태 false / GROUP_DELETED 로 변경)
        assertThat(participant.getStatus()).isEqualTo(ParticipantStatus.GROUP_DELETED);
        assertThat(participant.getActivated()).isFalse();
    }
}
