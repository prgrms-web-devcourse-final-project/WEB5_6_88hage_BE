package com.grepp.funfun.app.model.group.dto;

import com.grepp.funfun.app.model.group.code.GroupClassification;
import com.grepp.funfun.app.model.group.code.GroupStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupDTO {

    private Long id;

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String explain;

    @Size(max = 255)
    private String placeName;

    @Size(max = 255)
    private String address;

    private LocalDateTime groupDate;

    private Integer maxPeople;

    private Integer nowPeople;

    private GroupStatus status;

    @Size(max = 255)
    private String imageUrl;

    private Double latitude;

    private Double longitude;

    private Integer during;

    private GroupClassification category;

    @NotNull
    @Size(max = 255)
    private String leader;

    private List<String> hashTags;

}
