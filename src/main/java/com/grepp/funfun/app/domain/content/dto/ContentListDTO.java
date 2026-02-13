package com.grepp.funfun.app.domain.content.dto;

import com.grepp.funfun.app.domain.content.vo.EventType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentListDTO {

    private Long id;

    private String contentTitle;

    private String address;

    private LocalDate startDate;

    private LocalDate endDate;

    private String poster;

    @NotNull
    private String category;

    private EventType eventType;

    private Double latitude;

    private Double longitude;

}
