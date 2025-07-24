package com.grepp.funfun.app.domain.calendar.dto.payload;

import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarScheduleRequest {
    @NotNull
    private String email;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private ActivityType type;
}
