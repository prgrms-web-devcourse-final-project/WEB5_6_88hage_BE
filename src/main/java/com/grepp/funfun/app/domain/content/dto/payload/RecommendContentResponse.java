package com.grepp.funfun.app.domain.content.dto.payload;

import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendContentResponse {

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

    private String category;

    private List<String> images;

    private Integer bookmarkCount;
}
