package com.grepp.funfun.app.domain.calendar.dto.payload;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CalendarContentRequest {
    @NotNull
    private Long activityId;
    @NotNull
    private LocalDateTime selectedDate;
}
