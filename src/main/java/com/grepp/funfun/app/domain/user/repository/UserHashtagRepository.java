package com.grepp.funfun.app.domain.user.repository;

import com.grepp.funfun.app.domain.user.entity.UserHashtag;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserHashtagRepository extends JpaRepository<UserHashtag, Long> {

    void deleteByInfoEmail(String email);
}
