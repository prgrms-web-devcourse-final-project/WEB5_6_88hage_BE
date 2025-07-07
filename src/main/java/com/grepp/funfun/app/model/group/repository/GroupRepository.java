package com.grepp.funfun.app.model.group.repository;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupRepository extends JpaRepository<Group, Long>, GroupRepositoryCustom {

    Group findFirstByLeader(User user);

    List<Group> findByActivatedTrue();

    List<Group> findByEmailAndActivatedTrue(String email);


}
