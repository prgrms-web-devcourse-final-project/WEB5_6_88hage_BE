package com.grepp.funfun.app.domain.group.dto.payload;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class GroupResponse {

    private Long id;

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String explain;

    private String simpleExplain;

    @Size(max = 255)
    private String placeName;

    @Size(max = 255)
    private String address;

    private LocalDateTime groupDate;

    private Integer maxPeople;

    private Integer nowPeople;

    private GroupStatus status;

    private Double latitude;

    private Double longitude;

    private Integer during;

    private GroupClassification category;

    @NotNull
    @Size(max = 255)
    private String leader;

    private List<String> hashTags;

    private Boolean activated;

}
