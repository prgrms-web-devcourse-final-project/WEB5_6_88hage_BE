package com.grepp.funfun.app.model.recommend.dto;

import com.grepp.funfun.app.model.calendar.code.ActivityType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChatBotDTO {

    private Long id;

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String groupSummary;

    @Size(max = 255)
    private String contentSummary;

    private ActivityType type;

}
