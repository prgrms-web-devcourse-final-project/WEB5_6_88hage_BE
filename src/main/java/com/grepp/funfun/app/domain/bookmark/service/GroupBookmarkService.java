package com.grepp.funfun.app.domain.bookmark.service;

import com.grepp.funfun.app.domain.bookmark.dto.GroupBookmarkDTO;
import com.grepp.funfun.app.domain.bookmark.dto.payload.GroupBookmarkResponse;
import com.grepp.funfun.app.domain.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.domain.bookmark.repository.GroupBookmarkRepository;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupBookmarkService {

    private final GroupBookmarkRepository groupBookmarkRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    // 즐겨 찾기 추가
    @Transactional
    public void addGroupBookmark(Long groupId, String userEmail) {

        // 사용자 확인
        User user = userRepository.findById(userEmail)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "사용자를 찾을 수 없음"));

        // 그룹 확인
        Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "모임을 찾을 수 없음"));

        if(group.getActivated().equals(false)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "활성화 된 모임이 아니면 즐겨찾기 할 수 없습니다.");
        }

        if(group.getLeader().getEmail().equals(userEmail)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "자신이 만든 모임은 즐겨찾기 할 수 없습니다.");
        }

        // 중복 확인
        if (groupBookmarkRepository.existsByEmailAndGroup(userEmail, group)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "이미 즐겨찾기에 추가된 모임입니다.");
        }

        GroupBookmark bookmark = GroupBookmark.builder()
            .email(user.getEmail())
            .group(group)
            .build();

        groupBookmarkRepository.save(bookmark);
    }

    // 내 즐겨찾기 조회
    public List<GroupBookmarkResponse> getMyGroupBookMarks(String userEmail) {
        // 사용자 확인
        User user = userRepository.findById(userEmail)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "사용자를 찾을 수 없음"));

        List<GroupBookmark> bookmarks = groupBookmarkRepository.getMyGroupBookMarks(user.getEmail());
        return bookmarks.stream()
            .map(bookmark -> GroupBookmarkResponse.builder()
                .groupId(bookmark.getGroup().getId())
                .groupTitle(bookmark.getGroup().getTitle())
                .groupSimpleExplain(bookmark.getGroup().getSimpleExplain())
                .groupCategory(bookmark.getGroup().getCategory())
                .placeName(bookmark.getGroup().getPlaceName())
                .address(bookmark.getGroup().getAddress())
                .groupDate(bookmark.getGroup().getGroupDate())
                .maxPeople(bookmark.getGroup().getMaxPeople())
                .nowPeople(bookmark.getGroup().getNowPeople())
                .status(bookmark.getGroup().getStatus())
                .leaderEmail(bookmark.getGroup().getLeader().getEmail())
                .build())
            .collect(Collectors.toList());
    }

    //즐겨찾기 삭제
    @Transactional
    public void removeGroupBookmark(Long groupId, String userEmail){
        groupBookmarkRepository.deleteByEmailAndGroupId(userEmail, groupId);
    }

    public List<GroupBookmarkDTO> findAll() {
        final List<GroupBookmark> groupBookmarks = groupBookmarkRepository.findAll(
            Sort.by("id"));
        return groupBookmarks.stream()
            .map(groupBookmark -> mapToDTO(groupBookmark, new GroupBookmarkDTO()))
            .toList();
    }

    public GroupBookmarkDTO get(final Long id) {
        return groupBookmarkRepository.findById(id)
            .map(groupBookmark -> mapToDTO(groupBookmark, new GroupBookmarkDTO()))
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public void delete(final Long id) {
        groupBookmarkRepository.deleteById(id);
    }

    private GroupBookmarkDTO mapToDTO(final GroupBookmark groupBookmark,
        final GroupBookmarkDTO groupBookmarkDTO) {
        groupBookmarkDTO.setId(groupBookmark.getId());
        groupBookmarkDTO.setEmail(groupBookmark.getEmail());
        groupBookmarkDTO.setGroup(
            groupBookmark.getGroup() == null ? null : groupBookmark.getGroup().getId());
        return groupBookmarkDTO;
    }


}
