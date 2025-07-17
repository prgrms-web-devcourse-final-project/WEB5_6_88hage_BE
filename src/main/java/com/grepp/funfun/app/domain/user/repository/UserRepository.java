package com.grepp.funfun.app.domain.user.repository;

import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {

    User findFirstByInfo(UserInfo userInfo);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    User findByEmail(String email);

    List<User> findAllByStatus(UserStatus status);

    Optional<User> findByNickname(String nickname);

    Optional<User> findOptionalByEmail(String email);
}
