package com.grepp.funfun.app.domain.content.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContentDTO {

    private Long id;

    private String contentTitle;

    private String status;

    private String fee;

    private LocalDate startDate;

    private LocalDate endDate;

    private String address;

    private String reservationUrl;

    private String guName;

    private Integer runTime;

    private LocalTime startTime;

    private LocalTime endTime;

    @NotNull
    private Long category;

    private List<ContentImageDTO> images;

    private Integer bookmarkCount;

}
