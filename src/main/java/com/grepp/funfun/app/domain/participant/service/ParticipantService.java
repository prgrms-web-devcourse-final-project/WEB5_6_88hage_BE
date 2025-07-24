package com.grepp.funfun.app.domain.participant.service;

import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.dto.payload.GroupCompletedStatsResponse;
import com.grepp.funfun.app.domain.participant.dto.payload.ParticipantResponse;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CalendarService calendarService;

    //모임 참여 신청
    @Transactional
    public void apply(Long groupId, String userEmail){
        // 모임[모집중, True]
        Group group = groupRepository.findActiveRecruitingGroup(groupId)
            .orElseThrow(()-> new CommonException(ResponseCode.BAD_REQUEST, "모임 신청할 수 없습니다.[모집중 아님]"));

        // 사용자 검증
        User user = userRepository.findByEmail(userEmail);
        if(user == null || !user.getActivated()){
            throw new CommonException(ResponseCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        // 리더는 신청 불가하도록 검증
        if(group.getLeader().getEmail().equals(user.getEmail())){
            throw new CommonException(ResponseCode.BAD_REQUEST, "모임의 리더는 신청할 수 없습니다.");
        }

        // 기존 참여 이력 확인
        Optional<Participant> existingParticipant = participantRepository.findByUserAndGroup(user, group);

        if (existingParticipant.isPresent()) {
            ParticipantStatus status = existingParticipant.get().getStatus();

            switch (status) {
                case REJECTED ->
                    throw new CommonException(ResponseCode.BAD_REQUEST, "이미 거절 당한 모임입니다.");
                case PENDING ->
                    throw new CommonException(ResponseCode.BAD_REQUEST, "이미 신청한 모임입니다.");
                case APPROVED ->
                    throw new CommonException(ResponseCode.BAD_REQUEST, "이미 참여 중인 모임입니다.");
                case GROUP_KICKOUT ->
                    throw new CommonException(ResponseCode.BAD_REQUEST, "강제퇴장으로 인해 참여할 수 없습니다.");
                case LEAVE -> {
                    existingParticipant.get().changeLeaveStatusAndActivated(ParticipantStatus.PENDING);
                    participantRepository.save(existingParticipant.get());
                    return;
                }
            }
        }

        // todo : 프론트 확인 필요
//        // 모임 시간 체크
//        if(group.getGroupDate().isBefore(LocalDateTime.now())){
//            throw new CommonException(ResponseCode.BAD_REQUEST);
//        }

        // 참여자 생성
        Participant participant = Participant.builder()
            .user(user)
            .group(group)
            .role(ParticipantRole.MEMBER)
            .status(ParticipantStatus.PENDING)
            .build();

        participantRepository.save(participant);

    }

    //참여 승인
    @Transactional
    public void approveParticipant(Long groupId, List<String> userEmails, String leaderEmail){
        // 모임 검증
        Group group = validateActiveGroup(groupId);

        validateLeader(group, leaderEmail);

        // 최대 인원 체크
        int availableSpots = group.getMaxPeople() - group.getNowPeople();
        if(userEmails.size() > availableSpots) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "모임 최대 인원을 초과하였습니다.");
        }

        // 승인
        for(String userEmail : userEmails) {
            Participant participant = participantRepository.findByGroupIdAndUserEmail(groupId,userEmail).orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));
            participant.setStatus(ParticipantStatus.APPROVED);
            // 모임 생성 시 참여자의 캘린더에 자동으로 일정 추가하기
            calendarService.addGroupCalendar(userEmail, group);
        }
        // 인원 수 변경
        group.approveCount(userEmails.size());

        // 인원 >= 최대 인원 -> 모임 상태[모집중 -> 모집완료로 변경]
        if(group.getNowPeople() >= group.getMaxPeople()){
            group.changeStatus(GroupStatus.FULL);
        }

        groupRepository.save(group);
    }

    //참여 거절
    @Transactional
    public void rejectParticipant(Long groupId, List<String> userEmails, String leaderEmail) {
        Group group = validateActiveGroup(groupId);

        validateLeader(group, leaderEmail);

        // 거절
        for(String userEmail : userEmails) {
            Participant participant = participantRepository.findByGroupIdAndUserEmail(groupId,userEmail)
                .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND, "참가자를 찾을 수 없습니다."));

            participant.changeStatusAndActivated(ParticipantStatus.REJECTED);
            participantRepository.save(participant);
        }
    }
    //모임 강퇴
    @Transactional
    public void kickOut(Long groupId, String targetEmail, String leaderEmail) {
        // 모임 존재 확인
        Group group = validateActiveGroup(groupId);

        validateLeader(group, leaderEmail);

        // 강퇴 할 사용자
        Participant participant = participantRepository.findKickoutMember(groupId, targetEmail)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "참가자를 찾을 수 없습니다."));

        // 강제 퇴장 : status -GROUP_KICKOUT + 비활성화 처리
        participant.changeStatusAndActivated(ParticipantStatus.GROUP_KICKOUT);

        group.minusGroupCount();

        if(group.getMaxPeople()>group.getNowPeople()){
            group.changeStatus(GroupStatus.RECRUITING);
        }

        participantRepository.save(participant);

        // 추방된 사용자의 캘린더 모임 일정 제거
        calendarService.deleteGroupCalendarForUser(targetEmail, groupId);
    }

    // 모임 나가기
    @Transactional
    public void leave(Long groupId, String userEmail) {
        Group group = validateActiveGroup(groupId);

        // 검증(그룹에 속해있는 사용자가 맞는지)
        Participant participant = participantRepository.findTrueMember(groupId,userEmail)
            .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND,"모임에 속한 사용자가 아닙니다."));

        // 모임 나가기 : LEAVE 처리
        participant.changeStatusAndActivated(ParticipantStatus.LEAVE);

        group.minusGroupCount();

        if(group.getMaxPeople()>group.getNowPeople()){
            group.changeStatus(GroupStatus.RECRUITING);
        }

        participantRepository.save(participant);

        // 나간 사용자의 캘린더 모임 일정 제거
        calendarService.deleteGroupCalendarForUser(userEmail, groupId);
    }

    // 모임 신청한 사용자 조회
    public List<ParticipantResponse> getPendingParticipants(Long groupId) {
        validateActiveGroup(groupId);

        List<Participant> participants = participantRepository.findTruePendingMembers(groupId);

        return participants.stream()
            .map(ParticipantResponse::from)
            .collect(Collectors.toList());
    }

    // 모임 신청한 승인 사용자 조회
    public List<ParticipantResponse> getApproveParticipants(Long groupId) {
        validateActiveGroup(groupId);

        List<Participant> participants = participantRepository.findTrueApproveMembers(groupId);

        return participants.stream()
            .map(ParticipantResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countJoinGroupByEmail(String email) {
        return participantRepository.countByUserEmailAndRoleAndStatus(
            email,
            ParticipantRole.MEMBER,
            ParticipantStatus.GROUP_COMPLETE
        );
    }

    @Transactional(readOnly = true)
    public List<GroupCompletedStatsResponse> getGroupCompletionStats(String email) {
        List<GroupCompletedStatsResponse> stats = participantRepository.findGroupCompletedStats(email);

        return Arrays.stream(GroupClassification.values())
            .map(category ->
                stats.stream()
                    .filter(stat -> stat.getCategory() == category)
                    .findFirst()
                    .orElse(new GroupCompletedStatsResponse(category, 0L))
            )
            .toList();
    }

    public Group validateActiveGroup(Long groupId){
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if (!group.getActivated()) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }

        return group;
    }

    public void validateLeader(Group group, String leaderEmail){
        User leader = group.getLeader();

        if (!leader.getEmail().equals(leaderEmail)) {
            throw new CommonException(ResponseCode.UNAUTHORIZED, "권한이 없습니다.");
        }

        if(leader.getStatus() == UserStatus.SUSPENDED || leader.getStatus() == UserStatus.BANNED){
            throw new CommonException(ResponseCode.UNAUTHORIZED, "정지된 사용자입니다.");
        }
    }
}
