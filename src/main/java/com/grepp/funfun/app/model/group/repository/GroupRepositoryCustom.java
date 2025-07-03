package com.grepp.funfun.app.model.group.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepositoryCustom {

    Optional<Group> findActiveRecruitingGroup(Long groupId);
}
