package com.grepp.funfun.app.model.group.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.entity.GroupHashTag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupHashTagRepository extends JpaRepository<GroupHashTag, Long> {

    GroupHashTag findFirstByGroup(Group group);

}
