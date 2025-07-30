package com.grepp.funfun.app.domain.preference.repository;

import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupPreferenceRepository extends JpaRepository<GroupPreference, Long> {

    void deleteAllByUserEmail(String email);

    List<GroupPreference> findByUserEmail(String email);
}
