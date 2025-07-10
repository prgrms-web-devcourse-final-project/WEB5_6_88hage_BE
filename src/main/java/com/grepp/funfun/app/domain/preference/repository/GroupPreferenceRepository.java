package com.grepp.funfun.app.domain.preference.repository;

import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import com.grepp.funfun.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupPreferenceRepository extends JpaRepository<GroupPreference, Long> {

    GroupPreference findFirstByUser(User user);

    void deleteAllByUserEmail(String email);
}
