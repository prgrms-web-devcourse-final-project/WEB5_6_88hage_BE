package com.grepp.funfun.app.domain.group.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
import com.grepp.funfun.app.domain.calendar.entity.Calendar;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.domain.group.document.GroupDocument;
import com.grepp.funfun.app.domain.group.dto.GroupHashtagDTO;
import com.grepp.funfun.app.domain.group.dto.GroupParticipantDTO;
import com.grepp.funfun.app.domain.group.dto.GroupWithReasonDTO;
import com.grepp.funfun.app.domain.group.dto.payload.GroupDetailResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupListResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupMyResponse;
import com.grepp.funfun.app.domain.group.dto.payload.GroupRequest;
import com.grepp.funfun.app.domain.group.dto.payload.GroupSimpleResponse;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.GroupHashtag;
import com.grepp.funfun.app.domain.group.repository.GroupHashtagRepository;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.participant.repository.ParticipantRepository;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import com.grepp.funfun.app.domain.s3.service.S3FileService;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final GroupChatRoomRepository groupChatRoomRepository;
    private final CalendarRepository calendarRepository;
    private final GroupHashtagRepository groupHashtagRepository;
    private final CalendarService calendarService;
    private final RedisTemplate<String, String> redisTemplate;
    private final S3FileService s3FileService;
    private final ElasticsearchOperations elasticsearchOperations;



    // 모든 모임 조회
    public List<GroupDetailResponse> findAll() {
        final List<Group> groups = groupRepository.findAll();
        return groups.stream()
            .map(this::convertToGroupResponse)
            .toList();
    }

    // 모임 상세 조회
    @Transactional(readOnly = true)
    public GroupDetailResponse get(final Long groupId, String userEmail) {
        increaseViewCountIfNotCounted(groupId, userEmail);

        Group group = groupRepository.findByIdWithFullInfo(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "모임을 찾을 수 없습니다.(빈 값이 있을 경우 존재)"));

        // 관련 모임 2개 조회 (동일 카테고리, 거리순)
        List<GroupDetailResponse> relatedGroups = getRelatedGroups(group, userEmail);

        return GroupDetailResponse.fromWithRelated(group, relatedGroups);
    }

    // 관련 모임 조회 메소드 (기존 findGroups 활용)
    @Transactional(readOnly = true)
    public List<GroupDetailResponse> getRelatedGroups(Group currentGroup, String userEmail) {
        Pageable pageable = PageRequest.of(0, 3);
        Page<Group> relatedGroupsPage = groupRepository.findGroups(
            currentGroup.getCategory().toString(),
            null,
            "recent",
            userEmail,
            pageable
        );

        // 현재 그룹 제외하고 변환
        return relatedGroupsPage.getContent().stream()
            .filter(group -> !group.getId().equals(currentGroup.getId()))
            .limit(2)
            .map(group -> GroupDetailResponse.fromWithRelated(group, null))
            .collect(Collectors.toList());
    }

    //조회수 redis[중복 방지]
    public void increaseViewCountIfNotCounted(Long groupId, String userEmail) {

        if (userEmail == null || userEmail.trim().isEmpty()) {
            return;
        }
        //중복 확인(키)
        String key = "group:" + groupId + ":user:" + userEmail;

        boolean isCounted = redisTemplate.hasKey(key);
        if (!isCounted) {
            // 조회수 증가(키)
            // increment 를 통해 자동적으로 value +1 -> 값이 없으면 0으로 자동 설정
            redisTemplate.opsForValue().increment("group:" + groupId + ":viewCount");

            // 10분 유지
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(10));
        }
    }

    // 모임 조회
    @Transactional(readOnly = true)
    public Page<GroupListResponse> getGroups(
        String category,
        String keyword,
        String sortBy,
        String userEmail,
        Pageable pageable
    ) {
        Page<Group> groupPage = groupRepository.findGroups(category, keyword, sortBy, userEmail, pageable);

        return groupPage.map(GroupListResponse::convertToGroupList);

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
    public List<GroupSimpleResponse> findMyLeaderGroups(String userEmail) {
        return groupRepository.findByLeaderEmailAndActivatedTrue(userEmail).stream()
            .map(GroupSimpleResponse::toSimpleResponse)
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

        if (request.getHashTags() != null && !request.getHashTags().isEmpty()) {
            List<GroupHashtag> hashTags = request.getHashTags().stream()
                .map(tagName -> GroupHashtag.builder()
                    .tag(tagName)
                    .group(savedGroup)
                    .build())
                .toList();

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

            if (group.getImageUrl() != null) {
                s3FileService.delete(group.getImageUrl());
            }
            newImageUrl = s3FileService.upload(updateRequest.getImage(), "groups");
        }

        // 모임 변경 사항 저장
        group.applyUpdateFrom(updateRequest, newImageUrl);

        // 해시태그 설정
        updateHashtags(group, updateRequest.getHashTags());
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
            throw new CommonException(ResponseCode.BAD_REQUEST,"완료할 수 없는 모임 상태입니다.");
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
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "모임을 찾을 수 없습니다."));
    }

    // 사용자 검증
    private User validateUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CommonException(ResponseCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        if (user.getStatus() == UserStatus.SUSPENDED || user.getStatus() == UserStatus.BANNED) {
            throw new CommonException(ResponseCode.UNAUTHORIZED, "정지된 사용자 입니다.");
        }
        return user;
    }

    private GroupMyResponse convertToGroupMyResponse(Group group, String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail);

        return GroupMyResponse.builder()
            .groupId(group.getId())
            .groupTitle(group.getTitle())
            .groupLeaderEmail(group.getLeader().getEmail())
            .groupImageUrl(group.getImageUrl())
            .currentUserEmail(userEmail)
            .currenUserImageUrl(currentUser.getInfo().getImageUrl())
            .currentUserNickname(currentUser.getNickname())
            .participantCount(group.getNowPeople())
            .status(ParticipantStatus.APPROVED)
            .type(ChatRoomType.GROUP_CHAT)
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

    private GroupDetailResponse convertToGroupResponse(Group group) {
        return GroupDetailResponse.builder()
            .id(group.getId())
            .title(group.getTitle())
            .explain(group.getExplain())
            .simpleExplain(group.getSimpleExplain())
            .imageUrl(group.getImageUrl())
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
            .leaderExplain(group.getLeader().getInfo().getIntroduction())
            .leaderHashTags(group.getLeader().getGroupPreferences().stream()
                .map(GroupPreference::getCategory)
                .collect(Collectors.toList()))
            .hashTags(group.getHashtags().stream()
                .map(GroupHashtag::getTag)
                .collect(Collectors.toList()))
            .build();
    }

    @Transactional(readOnly = true)
    public List<GroupWithReasonDTO> findByIds(List<Long> recommendIds) {
        List<Group> groups = groupRepository.findGroupsByIdsWithAllRelations(recommendIds);

        return groups.stream().map(this::mapToDTO).toList();
    }

    private GroupWithReasonDTO mapToDTO(final Group group) {
        return GroupWithReasonDTO.builder()
                            .id(group.getId())
                            .title(group.getTitle())
                            .explain(group.getExplain())
                            .simpleExplain(group.getSimpleExplain())
                            .placeName(group.getPlaceName())
                            .address(group.getAddress())
                            .groupDate(group.getGroupDate())
                            .maxPeople(group.getMaxPeople())
                            .nowPeople(group.getNowPeople())
                            .imageUrl(group.getImageUrl())
                            .status(group.getStatus())
                            .latitude(group.getLatitude())
                            .longitude(group.getLongitude())
                            .during(group.getDuring())
                            .category(group.getCategory())
                            .leader(group.getLeader().getNickname())
                                 .participants(group.getParticipants().stream()
                                                    .map(participant -> GroupParticipantDTO.builder()
                                                                                           .id(participant.getId())
                                                                                           .role(participant.getRole())
                                                                                           .status(participant.getStatus())
                                                                                           .build())
                                                    .collect(Collectors.toList()))
                                 .hashtags(group.getHashtags().stream()
                                                .map(hashtag -> GroupHashtagDTO.builder()
                                                                               .id(hashtag.getId())
                                                                               .tag(hashtag.getTag())
                                                                               .group(hashtag.getGroup().getId()) // Group 엔티티의 ID
                                                                               .build())
                                                .collect(Collectors.toList()))
                            .build();
    }

    // ------------ ES ------------
    public Page<GroupListResponse> searchGroups(
        String query, String category, Pageable pageable
    ) {

        List<Query> mustQueries = new ArrayList<>();

        if (query != null && !query.isBlank()) {
            Query multiMatchQuery = MultiMatchQuery.of(m -> m
                .query(query)
                .fields("title^2", "simpleExplain^1")
                .fuzziness("AUTO")
            )._toQuery();

            mustQueries.add(multiMatchQuery);
        }

        List<Query> filters = new ArrayList<>();
        if (category != null && !category.isBlank()) {
            Query categoryFilter = TermQuery.of(t -> t
                .field("category")
                .value(category)
            )._toQuery();
            filters.add(categoryFilter);
        }

        Query boolQuery = BoolQuery.of(b -> b
            .must(mustQueries)
            .filter(filters)
        )._toQuery();

        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .withPageable(pageable)
            .build();

        SearchHits<GroupDocument> searchHits = this.elasticsearchOperations.search(nativeQuery,
            GroupDocument.class);

        return new PageImpl<>(searchHits.getSearchHits().stream()
            .map(hit -> {
                GroupDocument groupDocument = hit.getContent();
                return GroupListResponse.builder()
                    .id(Long.valueOf(groupDocument.getId()))
                    .title(groupDocument.getTitle())
                    .explain(groupDocument.getExplain())
                    .simpleExplain(groupDocument.getSimpleExplain())
                    .imageUrl(groupDocument.getImageUrl())
                    .placeName(groupDocument.getPlaceName())
                    .address(groupDocument.getAddress())
                    .viewCount(groupDocument.getViewCount())
                    .groupDate(LocalDateTime.parse(groupDocument.getGroupDate()))
                    .createdAt(LocalDateTime.parse(groupDocument.getCreatedAt()))
                    .maxPeople(groupDocument.getMaxPeople())
                    .nowPeople(groupDocument.getNowPeople())
                    .status(groupDocument.getStatus())
                    .during(groupDocument.getDuring())
                    .category(groupDocument.getCategory())
                    .activated(groupDocument.getActivated())
                    .build();
            }).toList());
    }
}
