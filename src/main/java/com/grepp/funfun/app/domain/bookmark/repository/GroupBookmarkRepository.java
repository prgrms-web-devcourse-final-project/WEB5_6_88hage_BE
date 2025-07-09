package com.grepp.funfun.app.domain.bookmark.repository;

import com.grepp.funfun.app.domain.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupBookmarkRepository extends JpaRepository<GroupBookmark, Long> {

    GroupBookmark findFirstByGroup(Group group);

}
