package com.grepp.funfun.app.model.user.repository;

import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {

    User findFirstByInfo(UserInfo userInfo);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
