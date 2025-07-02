package com.grepp.funfun.app.model.user.repository;

import com.grepp.funfun.app.model.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserInfoRepository extends JpaRepository<UserInfo, String> {

    boolean existsByEmailIgnoreCase(String email);

}
