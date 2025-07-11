package com.grepp.funfun.app.domain.group.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepositoryCustom {

    Optional<Group> findActiveRecruitingGroup(Long groupId);

    List<Group> findMyGroups(String userEmail);

    List<Group> findActiveRecentGroups();
    Optional<Group> findByIdWithFullInfo(Long groupId);
}
