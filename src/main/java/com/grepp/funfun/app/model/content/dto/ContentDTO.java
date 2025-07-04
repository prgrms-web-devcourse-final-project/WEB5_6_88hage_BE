package com.grepp.funfun.app.model.content.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class ContentDTO {

    private Long id;

    @Size(max = 255)
    private String contentTitle;

    @Size(max = 255)
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 255)
    private String address;

    private Double latitude;

    private Double longitude;

    @Size(max = 255)
    private String url;

    @Size(max = 255)
    private String imageUrl;

    @JsonProperty("isFree")
    private Boolean isFree;

    @Size(max = 255)
    private String guName;

    @NotNull
    private Long category;

}
