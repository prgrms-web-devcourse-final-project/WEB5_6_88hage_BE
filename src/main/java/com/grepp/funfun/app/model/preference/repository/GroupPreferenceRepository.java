package com.grepp.funfun.app.model.preference.repository;

import com.grepp.funfun.app.model.preference.entity.GroupPreference;
import com.grepp.funfun.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupPreferenceRepository extends JpaRepository<GroupPreference, Long> {

    GroupPreference findFirstByUser(User user);

}
