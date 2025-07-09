package com.grepp.funfun.app.domain.user.repository;

import com.grepp.funfun.app.domain.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

    boolean existsByEmailIgnoreCase(String email);

}
