package com.grepp.funfun.app.domain.bookmark.repository;

import com.grepp.funfun.app.domain.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupBookmarkRepository extends JpaRepository<GroupBookmark, Long>, GroupBookmarkRepositoryCustom{

    GroupBookmark findFirstByGroup(Group group);

    Boolean existsByEmailAndGroup(String userEmail, Group group);

    void deleteByEmailAndGroupId(String userEmail, Long groupId);

}
