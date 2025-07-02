package com.grepp.funfun.app.model.group.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findFirstByLeader(User user);

}
