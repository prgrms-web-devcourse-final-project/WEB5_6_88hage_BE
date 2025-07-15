package com.grepp.funfun.app.domain.user.repository;

import com.grepp.funfun.app.domain.user.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {

    @Query("SELECT u FROM User u left JOIN fetch u.contentPreferences WHERE u.email = :email")
    Optional<User> findByEmailWithContentPreferences(@Param("email") String email);

    @Query("SELECT u FROM User u left JOIN fetch u.groupPreferences WHERE u.email = :email")
    Optional<User> findByEmailWithGroupPreferences(@Param("email") String email);
}
