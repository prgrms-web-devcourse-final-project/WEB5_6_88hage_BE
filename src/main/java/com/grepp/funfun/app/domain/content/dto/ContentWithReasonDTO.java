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

    private String age;

    private String fee;

    private LocalDate startDate;

    private LocalDate endDate;

    private String address;

    private String guname;

    private String time;

    private String runTime;

    private String startTime;

    private String poster;

    private String description;

    @NotNull
    private String category;

    private List<ContentImageDTO> images;

    private List<ContentUrlDTO> urls;

    private EventType eventType;

    private Integer bookmarkCount;

    private String reason;

}
