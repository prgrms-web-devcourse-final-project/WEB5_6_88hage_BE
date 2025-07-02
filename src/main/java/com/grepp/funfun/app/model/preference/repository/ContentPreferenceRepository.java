package com.grepp.funfun.app.model.preference.repository;

import com.grepp.funfun.app.model.preference.entity.ContentPreference;
import com.grepp.funfun.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentPreferenceRepository extends JpaRepository<ContentPreference, Long> {

    ContentPreference findFirstByUser(User user);

}
