package com.grepp.funfun.app.domain.calendar.dto.payload;

import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalendarMonthlyResponse {
    private Long calendarId;
    private ActivityType type;
    private Long activityId;
    private String title;
    private LocalDateTime selectedDate;
}
