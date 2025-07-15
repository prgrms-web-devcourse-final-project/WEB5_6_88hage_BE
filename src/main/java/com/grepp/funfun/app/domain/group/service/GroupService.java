package com.grepp.funfun.app.domain.group.service;

import com.grepp.funfun.app.delete.util.ReferencedWarning;
import com.grepp.funfun.app.domain.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.domain.bookmark.repository.GroupBookmarkRepository;
import com.grepp.funfun.app.domain.calendar.entity.Calendar;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.domain.group.dto.payload.GroupListResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupMyResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupRequest;
import com.grepp.funfun.app.domain.group.dto.payload.GroupResponse;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.GroupHashtag;
import com.grepp.funfun.app.domain.group.repository.GroupHashtagRepository;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.s3.service.S3FileService;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final GroupChatRoomRepository groupChatRoomRepository;
    private final CalendarRepository calendarRepository;
    private final GroupHashtagRepository groupHashtagRepository;
    private final CalendarService calendarService;
    private final RedisTemplate<String, String> redisTemplate;
    private final S3FileService s3FileService;


    // 모든 모임 조회
    public List<GroupResponse> findAll() {
        final List<Group> groups = groupRepository.findAll();
        return groups.stream()
            .map(this::convertToGroupResponse)
            .toList();
    }

    // 모임 상세 조회
    @Transactional(readOnly = true)
    public GroupResponse get(final Long groupId, String userEmail) {
        increaseViewCountIfNotCounted(groupId, userEmail);
        return groupRepository.findByIdWithFullInfo(groupId)
            .map(this::convertToGroupResponse)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    //조회수 redis[중복 방지]
    public void increaseViewCountIfNotCounted(Long groupId, String userEmail) {

        if (userEmail == null || userEmail.trim().isEmpty()) {
            return;
        }

        String key = "group:viewCount:" + groupId + ":user:" + userEmail;

        boolean isCounted = redisTemplate.hasKey(key);
        if (!isCounted) {
            // 조회수 증가
            redisTemplate.opsForValue().increment("group:" + groupId + ":viewCount");

            // 현재 시간
            LocalDateTime now = LocalDateTime.now();

            // 다음 자정
            LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

            // 자정까지 남은 초
            long secondsUntilMidnight = Duration.between(now, nextMidnight).getSeconds();

            // 자정까지 TTL 설정
            redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(secondsUntilMidnight));
        }
    }

    // 모임 조회
    @Transactional(readOnly = true)
    public List<GroupListResponse> getGroups(
        String category,
        String keyword,
        String sortBy,
        String userEmail
    ) {
        return groupRepository.findGroups(category, keyword, sortBy, userEmail).stream()
            .map(GroupListResponse::convertToGroupList)
            .collect(Collectors.toList());
    }

    // 내가 속한 모임 조회
    @Transactional(readOnly = true)
    public List<GroupMyResponse> findMyGroups(String userEmail) {
        return groupRepository.findMyGroups(userEmail).stream()
            .map(group -> convertToGroupMyResponse(group, userEmail))
            .collect(Collectors.toList());
    }

    // 내가 리더인 모임 조회
    @Transactional(readOnly = true)
    public List<GroupResponse> findMyLeaderGroups(String userEmail) {
        return groupRepository.findByLeaderEmail(userEmail).stream()
            .map(this::convertToGroupResponse)
            .collect(Collectors.toList());
    }

    // 모임 생성
    @Transactional
    public void create(String leaderEmail, GroupRequest request) {

        User leader = validateUser(leaderEmail);
        // S3에 이미지 업로드
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = s3FileService.upload(request.getImage(), "groups");
        }

        Group savedGroup = groupRepository.save(request.mapToCreate(leader, imageUrl));

        // 해시태그
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
        GroupChatRoom groupChatRoom = GroupChatRoom.builder()
            .groupId(savedGroup.getId())
            .status(ChatRoomType.GROUP_CHAT)
            .name(savedGroup.getId() + "번 그룹 채팅방")
            .build();

        groupChatRoomRepository.save(groupChatRoom);

        // 모임 생성 시 리더의 캘린더에 자동으로 일정 추가하기
        calendarService.addGroupCalendar(leaderEmail, savedGroup);
    }

    // 모임 수정
    @Transactional
    public void update(Long groupId, String leaderEmail, GroupRequest updateRequest) {
        Group group = validateGroupWithLeader(groupId,leaderEmail);

        // 이미지 변경
        String newImageUrl = null;
        if (updateRequest.getImage() != null && !updateRequest.getImage().isEmpty()) {
            newImageUrl = s3FileService.upload(updateRequest.getImage(), "groups");
        }

        // 모임 변경 사항 저장
        Group updatedGroup = updateRequest.mapToUpdate(group, newImageUrl);
        Group savedGroup = groupRepository.save(updatedGroup);

        // 해시태그 설정
        updateHashtags(savedGroup, updateRequest.getHashTags());
    }

    // 모임 삭제
    @Transactional
    public void delete(Long groupId, String leaderEmail) {
        Group group = validateGroupWithLeader(groupId,leaderEmail);

        // 소프트 삭제 방식으로 -> false / DELETE 로 변경
        group.changeStatusAndActivated(GroupStatus.DELETE);

        // 관련 멤버들 active - false / status - GROUP_DELETED
        for (Participant participant : group.getParticipants()) {
            participant.changeStatusAndActivated(ParticipantStatus.GROUP_DELETED);
        }

        // 해당 모임 id 의 일정들 삭제
        calendarService.deleteGroupCalendar(groupId);
    }

    //모임 취소
    @Transactional
    public void cancel(Long groupId, String leaderEmail) {
        Group group = validateGroupWithLeader(groupId,leaderEmail);

        // 소프트 삭제 방식으로 -> false / CANCEL 로 변경
        group.changeStatusAndActivated(GroupStatus.CANCELED);

        // 관련 멤버들 active - false / status - GROUP_CANCELED
        for (Participant participant : group.getParticipants()) {
            participant.changeStatusAndActivated(ParticipantStatus.GROUP_CANCELED);
        }

        // 해당 모임 id 의 일정들 삭제
        calendarService.deleteGroupCalendar(groupId);
    }

    // 모임 완료
    @Transactional
    public void complete(Long groupId, String leaderEmail) {
        Group group = validateGroupWithLeader(groupId,leaderEmail);

        if (!Arrays.asList(GroupStatus.FULL, GroupStatus.RECRUITING).contains(group.getStatus())) {
            throw new IllegalStateException("완료할 수 없는 모임 상태입니다.");
        }

        group.changeStatusAndActivated(GroupStatus.COMPLETED);

        // 관련 멤버들 active - false / status - GROUP_CANCELED
        for (Participant participant : group.getParticipants()) {
            participant.unActivated();
            participant.setStatus(ParticipantStatus.GROUP_COMPLETE);
        }

    }

    // 그룹 확인 및 사용자 검증
    private Group validateGroupWithLeader(Long groupId, String userEmail) {
        Group group = validateGroup(groupId);
        User user = validateUser(userEmail);

        if (!group.getLeader().getEmail().equals(user.getEmail())) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        return group;
    }

    // 그룹 존재 확인
    private Group validateGroup(Long groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    // 사용자 검증
    private User validateUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CommonException(ResponseCode.NOT_FOUND);
        }

        if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.BANNED) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }
        return user;
    }

    private GroupMyResponse convertToGroupMyResponse(Group group, String userEmail) {
        // 내 참가자 정보 찾기
        Participant myParticipant = group.getParticipants().stream()
            .filter(p -> p.getUser().getEmail().equals(userEmail))
            .findFirst()
            .orElse(null);

        ParticipantStatus myStatus = myParticipant != null ? myParticipant.getStatus() : null;
        String userNickname = myParticipant != null ? myParticipant.getUser().getNickname() : null;

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
            .userNickname(userNickname)
            .status(myStatus)
            .type(ChatRoomType.GROUP_CHAT)
            .participantEmails(participantEmails)
            .participantNicknames(participantNicknames)
            .build();
    }

    public long countLeadGroupByEmail(String email) {
        return groupRepository.countByLeaderEmailAndStatus(email, GroupStatus.COMPLETED);
    }

    private void updateHashtags(Group group, List<String> newHashTags) {
        groupHashtagRepository.deleteByGroup(group);

        if (newHashTags != null && !newHashTags.isEmpty()) {
            List<GroupHashtag> hashtags = newHashTags.stream()
                .map(tagName -> GroupHashtag.builder()
                    .tag(tagName)
                    .group(group)
                    .build())
                .collect(Collectors.toList());
            groupHashtagRepository.saveAll(hashtags);
        }
    }

    private GroupResponse convertToGroupResponse(Group group) {
        return GroupResponse.builder()
            .id(group.getId())
            .title(group.getTitle())
            .explain(group.getExplain())
            .simpleExplain(group.getSimpleExplain())
            .placeName(group.getPlaceName())
            .address(group.getAddress())
            .groupDate(group.getGroupDate())
            .createdAt(group.getCreatedAt())
            .viewCount(group.getViewCount())
            .maxPeople(group.getMaxPeople())
            .nowPeople(group.getNowPeople())
            .status(group.getStatus())
            .latitude(group.getLatitude())
            .longitude(group.getLongitude())
            .during(group.getDuring())
            .category(group.getCategory())
            .activated(group.getActivated())
            .leaderNickname(group.getLeader().getNickname())
            .leaderEmail(group.getLeader().getEmail())
            .hashTags(group.getHashtags().stream()
                .map(GroupHashtag::getTag)
                .collect(Collectors.toList()))
            .build();
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
