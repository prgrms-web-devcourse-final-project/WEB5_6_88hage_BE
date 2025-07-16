package com.grepp.funfun.app.domain.content.dto;

import com.grepp.funfun.app.domain.content.vo.EventType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.*;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {

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

    private Double latitude;

    private Double longitude;

}
