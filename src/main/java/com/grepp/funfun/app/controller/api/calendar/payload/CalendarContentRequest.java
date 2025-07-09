package com.grepp.funfun.app.controller.api.calendar.payload;

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
