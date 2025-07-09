package com.grepp.funfun.app.domain.preference.repository;

import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import com.grepp.funfun.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentPreferenceRepository extends JpaRepository<ContentPreference, Long> {

    ContentPreference findFirstByUser(User user);

}
