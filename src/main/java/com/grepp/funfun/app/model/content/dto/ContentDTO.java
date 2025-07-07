package com.grepp.funfun.app.model.content.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


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

}
