package com.grepp.funfun.app.controller.api.calendar.payload;

import com.grepp.funfun.app.model.calendar.code.ActivityType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalendarMonthlyResponse {
    private Long calendarId;
    private ActivityType type;
    private String title;
    private LocalDateTime selectedDate;
}
