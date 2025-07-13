package com.grepp.funfun.app.domain.group.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepositoryCustom {

    // 모집중인 모임 조회
    Optional<Group> findActiveRecruitingGroup(Long groupId);

    // 내 모임 조회(채팅용)
    List<Group> findMyGroups(String userEmail);

    // 모임 조회
    List<Group> findGroups(String category, String keyword, String sortBy, String userEmail);

    // 모임 상세 조회
    Optional<Group> findByIdWithFullInfo(Long groupId);
}
