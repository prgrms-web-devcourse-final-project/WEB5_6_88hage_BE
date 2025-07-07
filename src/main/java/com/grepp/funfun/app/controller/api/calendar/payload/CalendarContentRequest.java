package com.grepp.funfun.app.controller.api.calendar.payload;

import com.grepp.funfun.app.model.calendar.code.ActivityType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CalendarContentRequest {
    @NotNull
    private ActivityType type;

    @AssertTrue(message = "이 요청은 ActivityType이 CONTENT인 경우에만 허용됩니다.")
    public boolean isType() {
        return this.type == ActivityType.CONTENT;
    }

    @NotNull
    private Long activityId;
    @NotNull
    private LocalDateTime selectedDate;
}
