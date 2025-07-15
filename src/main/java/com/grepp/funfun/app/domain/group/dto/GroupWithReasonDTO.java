package com.grepp.funfun.app.domain.group.dto;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import java.time.LocalDateTime;
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
public class GroupWithReasonDTO {

    private Long id;
    private String title;
    private String explain;
    private String simpleExplain;
    private String placeName;
    private String address;
    private LocalDateTime groupDate;
    private Integer maxPeople;
    private Integer nowPeople;
    private String imageUrl;
    private GroupStatus status;
    private Double latitude;
    private Double longitude;
    private Integer during;
    private GroupClassification category;
    private String leader;
    private List<GroupParticipantDTO> participants;
    private List<GroupHashtagDTO> hashtags;
    private String reason;
}
