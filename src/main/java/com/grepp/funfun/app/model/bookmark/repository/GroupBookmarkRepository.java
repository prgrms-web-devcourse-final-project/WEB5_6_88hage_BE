package com.grepp.funfun.app.model.bookmark.repository;

import com.grepp.funfun.app.model.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.model.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupBookmarkRepository extends JpaRepository<GroupBookmark, Long> {

    GroupBookmark findFirstByGroup(Group group);

}
