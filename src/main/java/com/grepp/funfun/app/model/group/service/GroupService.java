package com.grepp.funfun.app.model.group.service;

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
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import com.grepp.funfun.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupBookmarkRepository groupBookmarkRepository;
    private final ParticipantRepository participantRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final CalendarRepository calendarRepository;
    private final GroupHashtagRepository groupHashtagRepository;

    public GroupService(final GroupRepository groupRepository, final UserRepository userRepository,
            final GroupBookmarkRepository groupBookmarkRepository,
            final ParticipantRepository participantRepository,
            final ChatRoomRepository chatRoomRepository,
            final CalendarRepository calendarRepository,
            final GroupHashtagRepository groupHashtagRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupBookmarkRepository = groupBookmarkRepository;
        this.participantRepository = participantRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.calendarRepository = calendarRepository;
        this.groupHashtagRepository = groupHashtagRepository;
    }

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

    public Long create(final GroupDTO groupDTO) {
        final Group group = new Group();
        mapToEntity(groupDTO, group);
        return groupRepository.save(group).getId();
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
        groupDTO.setImageUrl(group.getImageUrl());
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
        group.setImageUrl(groupDTO.getImageUrl());
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
