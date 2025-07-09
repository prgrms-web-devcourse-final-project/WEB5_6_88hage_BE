package com.grepp.funfun.app.domain.calendar.repository;

import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarDailyResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarMonthlyResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarRepositoryCustom {
    List<CalendarMonthlyResponse> findMonthlyContentCalendars(String email, LocalDateTime start, LocalDateTime end);
    List<CalendarMonthlyResponse> findMonthlyGroupCalendars(String email, LocalDateTime start, LocalDateTime end);
    List<CalendarDailyResponse> findDailyContentCalendars(String email, LocalDateTime start, LocalDateTime end);
    List<CalendarDailyResponse> findDailyGroupCalendars(String email, LocalDateTime start, LocalDateTime end);
}
