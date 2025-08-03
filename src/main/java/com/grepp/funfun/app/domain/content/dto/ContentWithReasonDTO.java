package com.grepp.funfun.app.domain.content.dto;

import com.grepp.funfun.app.domain.content.vo.EventType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentWithReasonDTO {

    private Long id;
    private String contentTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String address;
    private String poster;
    private EventType eventType;
    private String reason;

}
