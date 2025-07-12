package com.grepp.funfun.app.domain.group.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepositoryCustom {

    // 모집중인 모임 조회
    Optional<Group> findActiveRecruitingGroup(Long groupId);

    // 내 모임 조회
    List<Group> findMyGroups(String userEmail);

    // 최신순
    List<Group> findActiveRecentGroups();

    // 키워드 조회(검색)
    List<Group> findGroupsByKeyword(String keyword);

    // 가까운 순 조회
    List<Group> findNearbyGroups(Double userLat, Double userLng);

    // 특정 모임 조회
    Optional<Group> findByIdWithFullInfo(Long groupId);
}
