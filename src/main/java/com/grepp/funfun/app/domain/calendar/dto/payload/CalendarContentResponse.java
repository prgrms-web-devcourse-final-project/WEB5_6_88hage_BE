package com.grepp.funfun.app.domain.calendar.dto.payload;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalendarContentResponse {
    private Long calendarId;
    private Long contentId;
    private String contentTitle;
    private ContentClassification category;
    private LocalDateTime selectedDate;
}
