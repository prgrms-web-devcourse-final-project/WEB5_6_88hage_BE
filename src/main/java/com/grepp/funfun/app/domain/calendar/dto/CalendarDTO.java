package com.grepp.funfun.app.domain.calendar.dto;

import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CalendarDTO {

    private Long id;

    @Size(max = 255)
    private String email;

    private LocalDateTime selectedDate;

    private ActivityType type;

    private Long content;

    private Long group;

}
