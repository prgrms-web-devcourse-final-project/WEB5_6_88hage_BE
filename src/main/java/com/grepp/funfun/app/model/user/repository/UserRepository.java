package com.grepp.funfun.app.model.user.repository;

import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {

    User findFirstByInfo(UserInfo userInfo);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
    boolean existsByInfoEmailIgnoreCase(String email);

    User findByEmail(String email);

    Optional<User> findByNickname(String nickname);
}
