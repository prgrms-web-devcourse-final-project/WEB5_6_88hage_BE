package com.grepp.funfun.app.domain.group.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.GroupHashtag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupHashtagRepository extends JpaRepository<GroupHashtag, Long> {

    GroupHashtag findFirstByGroup(Group group);

}
