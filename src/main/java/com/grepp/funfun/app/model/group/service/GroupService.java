package com.grepp.funfun.app.model.group.service;

import com.grepp.funfun.app.controller.api.group.payload.GroupMyResponse;
import com.grepp.funfun.app.controller.api.group.payload.GroupRequest;
import com.grepp.funfun.app.controller.api.group.payload.GroupResponse;
import com.grepp.funfun.app.model.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.model.bookmark.repository.GroupBookmarkRepository;
import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.model.calendar.service.CalendarService;
import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import com.grepp.funfun.app.model.chat.repository.ChatRoomRepository;
import com.grepp.funfun.app.model.group.code.GroupStatus;
import com.grepp.funfun.app.model.group.dto.GroupDTO;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.entity.GroupHashtag;
import com.grepp.funfun.app.model.group.repository.GroupHashtagRepository;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.app.model.participant.code.ParticipantRole;
import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;
import java.util.Arrays;
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
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupBookmarkRepository groupBookmarkRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CalendarRepository calendarRepository;
    private final GroupHashtagRepository groupHashtagRepository;
    private final CalendarService calendarService;

    public List<GroupDTO> findAll() {
        final List<Group> groups = groupRepository.findAll(Sort.by("id"));
        return groups.stream()
            .map(group -> mapToDTO(group, new GroupDTO()))
            .toList();
    }

    public GroupDTO get(final Long id) {
        return groupRepository.findById(id)
            .map(group -> mapToDTO(group, new GroupDTO()))
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }
    // 모든 모임 조회
    @Transactional(readOnly = true)
    public List<GroupResponse> findByActivatedTrue(){
        return groupRepository.findByActivatedTrue().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    // 내가 속한 모임 조회
    @Transactional(readOnly = true)
    public List<GroupMyResponse> findMyGroups(String userEmail) {
        return groupRepository.findMyGroups(userEmail).stream()
            .map(group -> convertToGroupMyResponse(group, userEmail))
            .collect(Collectors.toList());
    }

    // 모임 생성
    @Transactional
    public void create(String leaderEmail, GroupRequest request) {

        User leader = userRepository.findByEmail(leaderEmail);

        if (leader == null || !leader.getActivated()) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }
        Group savedGroup = groupRepository.save(request.toEntity(leader));

        // HashTag 저장
        if (request.getHashTags() != null && !request.getHashTags().isEmpty()) {
            List<GroupHashtag> hashTags = request.getHashTags().stream()
                .map(tagName -> {
                    GroupHashtag hashTag = new GroupHashtag();
                    hashTag.setTag(tagName);
                    hashTag.setGroup(savedGroup);
                    return hashTag;
                })
                .collect(Collectors.toList());

            groupHashtagRepository.saveAll(hashTags);
        }
        // 모임 생성 시 자동으로 참여자에 리더로 넣기
        Participant leaderParticipant = Participant.builder()
            .user(leader)
            .group(savedGroup)
            .role(ParticipantRole.LEADER)
            .status(ParticipantStatus.APPROVED)
            .build();
        participantRepository.save(leaderParticipant);

        // 모임 생성 시 자동으로 팀 채팅방 생성하기
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setGroup(savedGroup);
        chatRoom.setName(savedGroup.getId() + "번 채팅방");
        chatRoomRepository.save(chatRoom);

        // 모임 생성 시 리더의 캘린더에 자동으로 일정 추가하기
        calendarService.addGroupCalendar(leaderEmail, savedGroup);
    }


    // 모임 수정
    @Transactional
    public void update(Long groupId, String leaderEmail, GroupRequest updateRequest) {
        Group group = groupWithLeaderValidation(groupId, leaderEmail);

        //Builder 패턴으로 내용 수정하고 다시 저장하기
        group.setTitle(updateRequest.getTitle());
        group.setExplain(updateRequest.getExplain());
        group.setPlaceName(updateRequest.getPlaceName());
        group.setGroupDate(updateRequest.getGroupDate());
        group.setAddress(updateRequest.getAddress());
        group.setCategory(updateRequest.getCategory());
        group.setMaxPeople(updateRequest.getMaxPeople());
        group.setLatitude(updateRequest.getLatitude());
        group.setLongitude(updateRequest.getLongitude());
        group.setDuring(updateRequest.getDuring());

        // 재설정
        group.getHashtags().clear();
        List<GroupHashtag> hashtags = updateRequest.getHashTags().stream()
            .map(hashtagName -> GroupHashtag.builder()
                .tag(hashtagName)
                .group(group)
                .build())
            .collect(Collectors.toList());
        group.setHashtags(hashtags);

    }

    // 모임 삭제
    @Transactional
    public void delete(Long groupId, String leaderEmail) {
        Group group = groupWithLeaderValidation(groupId, leaderEmail);

        // 소프트 삭제 방식으로 -> false / DELETE 로 변경
        group.unActivated();
        group.setStatus(GroupStatus.DELETE);

        // 관련 멤버들 active - false / status - GROUP_DELETED
        for (Participant participant : group.getParticipants()) {
            participant.unActivated();
            participant.setStatus(ParticipantStatus.GROUP_DELETED);
        }

        // 해당 모임 id 의 일정들 삭제
        calendarService.deleteGroupCalendar(groupId);
    }

    //모임 취소
    @Transactional
    public void cancel(Long groupId, String leaderEmail) {
        Group group = groupWithLeaderValidation(groupId, leaderEmail);

        // 소프트 삭제 방식으로 -> false / CANCEL 로 변경
        group.unActivated();
        group.setStatus(GroupStatus.CANCELED);

        // 관련 멤버들 active - false / status - GROUP_CANCELED
        for (Participant participant : group.getParticipants()) {
            participant.unActivated();
            participant.setStatus(ParticipantStatus.GROUP_CANCELED);
        }

        // 해당 모임 id 의 일정들 삭제
        calendarService.deleteGroupCalendar(groupId);
    }

    // 모임 완료
    @Transactional
    public void complete(Long groupId, String leaderEmail) {
        Group group = groupWithLeaderValidation(groupId, leaderEmail);

        if (!Arrays.asList(GroupStatus.FULL, GroupStatus.RECRUITING).contains(group.getStatus())) {
            throw new IllegalStateException("완료할 수 없는 모임 상태입니다.");
        }

        group.unActivated();
        group.setStatus(GroupStatus.COMPLETED);

        // 관련 멤버들 active - false / status - GROUP_CANCELED
        for (Participant participant : group.getParticipants()) {
            participant.unActivated();
            participant.setStatus(ParticipantStatus.GROUP_COMPLETE);
        }

    }
    // 그룹에 리더이자 사용자 false 상태 확인 코드(중복 코드 대체)
    private Group groupWithLeaderValidation(Long groupId, String leaderEmail) {
        // 그룹 존재 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        // 리더인지 확인
        User leader = group.getLeader();
        if (!leader.getEmail().equals(leaderEmail)) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }
        // 사용자로 false (정지 당한 사람이 아닌지)
        if (!leader.getActivated()) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }
        return group;
    }

    private GroupMyResponse convertToGroupMyResponse(Group group, String userEmail) {
        // 내 상태 찾기
        ParticipantStatus myStatus = group.getParticipants().stream()
            .filter(p -> p.getUser().getEmail().equals(userEmail))
            .findFirst()
            .map(Participant::getStatus)
            .orElse(null);

        // 참여자 이메일들 추출
        List<String> participantEmails = group.getParticipants().stream()
            .map(p -> p.getUser().getEmail())
            .collect(Collectors.toList());

        // 참여자 닉네임들 추출
        List<String> participantNicknames = group.getParticipants().stream()
            .map(p -> p.getUser().getNickname())
            .collect(Collectors.toList());

        return GroupMyResponse.builder()
            .groupId(group.getId())
            .groupTitle(group.getTitle())
            .userEmail(userEmail)
            .status(myStatus)
            .participantEmails(participantEmails)
            .participantNicknames(participantNicknames)
            .build();
    }

    private GroupResponse mapToResponse(final Group group) {
        GroupResponse response = new GroupResponse();

        response.setId(group.getId());
        response.setTitle(group.getTitle());
        response.setExplain(group.getExplain());
        response.setPlaceName(group.getPlaceName());
        response.setAddress(group.getAddress());
        response.setGroupDate(group.getGroupDate());
        response.setMaxPeople(group.getMaxPeople());
        response.setNowPeople(group.getNowPeople());
        response.setStatus(group.getStatus());
        response.setLatitude(group.getLatitude());
        response.setLongitude(group.getLongitude());
        response.setDuring(group.getDuring());
        response.setCategory(group.getCategory());
        response.setLeader(group.getLeader() == null ? null : group.getLeader().getEmail());
        response.setActivated(group.getActivated());

        // 해시태그 변환
        List<String> hashTags = group.getHashtags().stream()
            .map(GroupHashtag::getTag)
            .collect(Collectors.toList());
        response.setHashTags(hashTags);

        return response;
    }

    private GroupDTO mapToDTO(final Group group, final GroupDTO groupDTO) {
        groupDTO.setId(group.getId());
        groupDTO.setTitle(group.getTitle());
        groupDTO.setExplain(group.getExplain());
        groupDTO.setPlaceName(group.getPlaceName());
        groupDTO.setAddress(group.getAddress());
        groupDTO.setGroupDate(group.getGroupDate());
        groupDTO.setMaxPeople(group.getMaxPeople());
        groupDTO.setNowPeople(group.getNowPeople());
        groupDTO.setStatus(group.getStatus());
        groupDTO.setLatitude(group.getLatitude());
        groupDTO.setLongitude(group.getLongitude());
        groupDTO.setDuring(group.getDuring());
        groupDTO.setCategory(group.getCategory());
        groupDTO.setLeader(group.getLeader() == null ? null : group.getLeader().getEmail());
        return groupDTO;
    }

    private Group mapToEntity(final GroupDTO groupDTO, final Group group) {
        group.setTitle(groupDTO.getTitle());
        group.setExplain(groupDTO.getExplain());
        group.setPlaceName(groupDTO.getPlaceName());
        group.setAddress(groupDTO.getAddress());
        group.setGroupDate(groupDTO.getGroupDate());
        group.setMaxPeople(groupDTO.getMaxPeople());
        group.setNowPeople(groupDTO.getNowPeople());
        group.setStatus(groupDTO.getStatus());
        group.setLatitude(groupDTO.getLatitude());
        group.setLongitude(groupDTO.getLongitude());
        group.setDuring(groupDTO.getDuring());
        group.setCategory(groupDTO.getCategory());
        final User leader =
            groupDTO.getLeader() == null ? null : userRepository.findById(groupDTO.getLeader())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        group.setLeader(leader);
        return group;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Group group = groupRepository.findById(id)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        final GroupBookmark groupGroupBookmark = groupBookmarkRepository.findFirstByGroup(group);
        if (groupGroupBookmark != null) {
            referencedWarning.setKey("group.groupBookmark.group.referenced");
            referencedWarning.addParam(groupGroupBookmark.getId());
            return referencedWarning;
        }
        final Participant groupParticipant = participantRepository.findFirstByGroup(group);
        if (groupParticipant != null) {
            referencedWarning.setKey("group.participant.group.referenced");
            referencedWarning.addParam(groupParticipant.getId());
            return referencedWarning;
        }
        final ChatRoom groupChatRoom = chatRoomRepository.findFirstByGroup(group);
        if (groupChatRoom != null) {
            referencedWarning.setKey("group.chatRoom.group.referenced");
            referencedWarning.addParam(groupChatRoom.getId());
            return referencedWarning;
        }
        final Calendar groupCalendar = calendarRepository.findFirstByGroup(group);
        if (groupCalendar != null) {
            referencedWarning.setKey("group.calendar.group.referenced");
            referencedWarning.addParam(groupCalendar.getId());
            return referencedWarning;
        }
        final GroupHashtag groupGroupHashtag = groupHashtagRepository.findFirstByGroup(group);
        if (groupGroupHashtag != null) {
            referencedWarning.setKey("group.groupHashtag.group.referenced");
            referencedWarning.addParam(groupGroupHashtag.getId());
            return referencedWarning;
        }
        return null;
    }

}
