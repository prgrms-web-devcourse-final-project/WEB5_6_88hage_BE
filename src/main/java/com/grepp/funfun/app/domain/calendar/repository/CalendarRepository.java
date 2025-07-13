package com.grepp.funfun.app.domain.calendar.repository;

import com.grepp.funfun.app.domain.calendar.entity.Calendar;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.group.entity.Group;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CalendarRepository extends JpaRepository<Calendar, Long>, CalendarRepositoryCustom {

    Calendar findFirstByContent(Content content);

    Calendar findFirstByGroup(Group group);

    Optional<Calendar> findByIdAndEmail(Long id, String email);

    void deleteByGroupId(Long groupId);

    void deleteByEmailAndGroupId(String email, Long groupId);

    boolean existsByEmailAndContentIdAndSelectedDate(String email, Long contentId, LocalDateTime selectedDate);
}
