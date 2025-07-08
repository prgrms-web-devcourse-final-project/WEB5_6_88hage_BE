package com.grepp.funfun.app.model.calendar.repository;

import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.group.entity.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    Calendar findFirstByContent(Content content);

    Calendar findFirstByGroup(Group group);

    Optional<Calendar> findByIdAndEmail(Long id, String email);
}
