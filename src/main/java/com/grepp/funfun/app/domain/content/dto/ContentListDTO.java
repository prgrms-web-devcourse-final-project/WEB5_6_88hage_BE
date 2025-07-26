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

    private String fee;

    private LocalDate startDate;

    private LocalDate endDate;

    private String guname;

    private String poster;

    @NotNull
    private String category;

    private EventType eventType;

    private Integer bookmarkCount;

    private Double latitude;

    private Double longitude;

}
