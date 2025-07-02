package com.grepp.funfun.app.model.group.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.entity.GroupHashtag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupHashtagRepository extends JpaRepository<GroupHashtag, Long> {

    GroupHashtag findFirstByGroup(Group group);

}
