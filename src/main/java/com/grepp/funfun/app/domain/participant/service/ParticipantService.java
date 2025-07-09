package com.grepp.funfun.app.domain.participant.service;

import com.grepp.funfun.app.domain.participant.dto.payload.ParticipantResponse;
import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.participant.dto.ParticipantDTO;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
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
            .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));

        // 사용자 검증
        User user = userRepository.findByEmail(userEmail);
        if(user == null || !user.getActivated()){
            throw new CommonException(ResponseCode.NOT_FOUND);
        }

        // 리더는 신청 불가하도록 검증
        if(group.getLeader().getEmail().equals(user.getEmail())){
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        // 중복 신청
        boolean alreadyApplied = participantRepository.existsByUserAndGroup(user,group);
        if(alreadyApplied){
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

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
        Group group = groupRepository.findById(groupId)
            .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));

        // 2. 활성화 상태 확인
        if (!group.getActivated()) {
            throw new CommonException(ResponseCode.USER_SUSPENDED); // 또는 적절한 에러코드
        }

        // 리더 검증
        User leader = group.getLeader();

        if (!leader.getEmail().equals(leaderEmail) || !leader.getActivated()) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        // 최대 인원 체크
        int availableSpots = group.getMaxPeople() - group.getNowPeople();
        if(userEmails.size() > availableSpots) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        // 승인
        for(String userEmail : userEmails) {
            Participant participant = participantRepository.findByGroupIdAndUserEmail(groupId,userEmail).orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));
            log.info(participant.toString());
            participant.setStatus(ParticipantStatus.APPROVED);
            // 모임 생성 시 참여자의 캘린더에 자동으로 일정 추가하기
            calendarService.addGroupCalendar(userEmail, group);
        }
        // 인원 수 변경
        group.setNowPeople(group.getNowPeople() + userEmails.size());

        // 인원 >= 최대 인원 -> 모임 상태[모집중 -> 모집완료로 변경]
        if(group.getNowPeople() >= group.getMaxPeople()){
            group.setStatus(GroupStatus.FULL);
        }

        groupRepository.save(group);
    }

    //참여 거절
    @Transactional
    public void rejectParticipant(Long groupId, List<String> userEmails, String leaderEmail) {
        // 1. 그룹 존재 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 2. 활성화 상태 확인
        if (!group.getActivated()) {
            throw new CommonException(ResponseCode.USER_SUSPENDED); // 또는 적절한 에러코드
        }

        // 3. 리더 검증
        User leader = group.getLeader();

        if (!leader.getEmail().equals(leaderEmail) || !leader.getActivated()) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        // 4. 거절
        for(String userEmail : userEmails) {
            Participant participant = participantRepository.findByGroupIdAndUserEmail(groupId,userEmail)
                .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));

            log.info(participant.toString());
            participant.setStatus(ParticipantStatus.REJECTED);
            participantRepository.save(participant);
        }
    }
    //모임 강퇴
    @Transactional
    public void kickOut(Long groupId, String targetEmail, String leaderEmail) {
        // 1. 그룹 존재 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 2. 활성화 상태 확인
        if (!group.getActivated()) {
            throw new CommonException(ResponseCode.USER_SUSPENDED); // 또는 적절한 에러코드
        }

        // 3. 리더 검증
        User leader = group.getLeader();

        if (!leader.getEmail().equals(leaderEmail) || !leader.getActivated()) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        // 4. 강퇴 할 사용자
        Participant participant = participantRepository.findKickoutMember(groupId, targetEmail)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "참가자를 찾을 수 없습니다."));

        // 5. 강제 퇴장 : status -GROUP_KICKOUT + 비활성화 처리
        participant.setStatus(ParticipantStatus.GROUP_KICKOUT);
        participant.unActivated();

        group.setNowPeople(group.getNowPeople() - 1);

        participantRepository.save(participant);

        // 추방된 사용자의 캘린더 모임 일정 제거
        calendarService.deleteGroupCalendarForUser(targetEmail, groupId);
    }

    // 모임 나가기
    @Transactional
    public void leave(Long groupId, String userEmail) {
        // 1. 모임 존재 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 2. 검증(그룹에 속해있는 사용자가 맞는지)
        Participant participant = participantRepository.findTrueMember(groupId,userEmail)
            .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));

        // 3. 모임 나가기 : 비활성화 처리 + LEAVE 처리
        participant.unActivated();
        participant.setStatus(ParticipantStatus.LEAVE);

        group.setNowPeople(group.getNowPeople() - 1);

        participantRepository.save(participant);

        // 나간 사용자의 캘린더 모임 일정 제거
        calendarService.deleteGroupCalendarForUser(userEmail, groupId);
    }

    // 모임 신청한 사용자 조회
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getPendingParticipants(Long groupId) {
        // 1. 모임 존재 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 2. 활성화 상태 확인
        if (!group.getActivated()) {
            throw new CommonException(ResponseCode.USER_SUSPENDED); // 또는 적절한 에러코드
        }
        List<Participant> participants = participantRepository.findTruePendingMembers(groupId);

        return participants.stream()
            .map(ParticipantResponse::from)
            .collect(Collectors.toList());
    }

    // 모임 신청한 승인 조회
    @Transactional(readOnly = true)
    public List<ParticipantResponse> getApproveParticipants(Long groupId) {
        // 1. 모임 존재 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 2. 활성화 상태 확인
        if (!group.getActivated()) {
            throw new CommonException(ResponseCode.USER_SUSPENDED); // 또는 적절한 에러코드
        }

        List<Participant> participants = participantRepository.findTrueApproveMembers(groupId);

        return participants.stream()
            .map(ParticipantResponse::from)
            .collect(Collectors.toList());
    }
    // ------------------------------여기 까지 ------------------------------------

    public List<ParticipantDTO> findAll() {
        final List<Participant> participants = participantRepository.findAll(Sort.by("id"));
        return participants.stream()
                .map(participant -> mapToDTO(participant, new ParticipantDTO()))
                .toList();
    }

    public ParticipantDTO get(final Long id) {
        return participantRepository.findById(id)
                .map(participant -> mapToDTO(participant, new ParticipantDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ParticipantDTO participantDTO) {
        final Participant participant = new Participant();
        mapToEntity(participantDTO, participant);
        return participantRepository.save(participant).getId();
    }

    public void update(final Long id, final ParticipantDTO participantDTO) {
        final Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(participantDTO, participant);
        participantRepository.save(participant);
    }

    public void delete(final Long id) {
        participantRepository.deleteById(id);
    }

    private ParticipantDTO mapToDTO(final Participant participant,
            final ParticipantDTO participantDTO) {
        participantDTO.setId(participant.getId());
        participantDTO.setRole(participant.getRole());
        participantDTO.setStatus(participant.getStatus());
        participantDTO.setUser(participant.getUser() == null ? null : participant.getUser().getEmail());
        participantDTO.setGroup(participant.getGroup() == null ? null : participant.getGroup().getId());
        return participantDTO;
    }

    private Participant mapToEntity(final ParticipantDTO participantDTO,
            final Participant participant) {
        participant.setRole(participantDTO.getRole());
        participant.setStatus(participantDTO.getStatus());
        final User user = participantDTO.getUser() == null ? null : userRepository.findById(participantDTO.getUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        participant.setUser(user);
        final Group group = participantDTO.getGroup() == null ? null : groupRepository.findById(participantDTO.getGroup())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        participant.setGroup(group);
        return participant;
    }

}
