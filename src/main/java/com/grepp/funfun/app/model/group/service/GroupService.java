package com.grepp.funfun.app.model.group.service;

import com.grepp.funfun.app.controller.api.group.payload.GroupRequest;
import com.grepp.funfun.app.model.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.model.bookmark.repository.GroupBookmarkRepository;
import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import com.grepp.funfun.app.model.chat.repository.ChatRoomRepository;
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
    // 모임 생성
    @Transactional
    public void create(String leaderEmail,GroupRequest request) {

        User leader = userRepository.findByEmail(leaderEmail);

        if(leader == null || !leader.getActivated()){
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
        chatRoom.setName(savedGroup.getId()+"번 채팅방");
        chatRoomRepository.save(chatRoom);
    }
    //모임 참여 신청
    @Transactional
    public void apply(Long groupId, String userEmail){
        // 모임[모집중, True]
        Group group = groupRepository.findActiveRecruitingGroup(groupId)
            .orElseThrow(()-> new CommonException(ResponseCode.NOT_FOUND));

        // 사용자 검증
        User user = userRepository.findByEmail(userEmail);
        if(user == null){
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

    public void update(final Long id, final GroupDTO groupDTO) {
        final Group group = groupRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(groupDTO, group);
        groupRepository.save(group);
    }

    public void delete(final Long id) {
        groupRepository.deleteById(id);
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
        final User leader = groupDTO.getLeader() == null ? null : userRepository.findById(groupDTO.getLeader())
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
