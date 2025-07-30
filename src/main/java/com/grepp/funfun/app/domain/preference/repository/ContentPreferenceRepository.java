package com.grepp.funfun.app.domain.preference.repository;

import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentPreferenceRepository extends JpaRepository<ContentPreference, Long> {

    void deleteAllByUserEmail(String email);

   List<ContentPreference> findByUserEmail(String email);
}
