package com.grepp.funfun.app.domain.group.dto.payload;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class GroupListResponse {

    private Long id;

    private String title;

    private String explain;

    private String simpleExplain;

    private String imageUrl;

    private String placeName;

    private String address;

    private Integer viewCount;

    private LocalDateTime groupDate;

    private LocalDateTime createdAt;

    private Integer maxPeople;

    private Integer nowPeople;

    private GroupStatus status;

    private Integer during;

    private GroupClassification category;

    private Boolean activated;

    public static GroupListResponse convertToGroupList(Group group) {
        return GroupListResponse.builder()
            .id(group.getId())
            .title(group.getTitle())
            .explain(group.getExplain())
            .simpleExplain(group.getSimpleExplain())
            .imageUrl(group.getImageUrl())
            .placeName(group.getPlaceName())
            .address(group.getAddress())
            .viewCount(group.getViewCount())
            .groupDate(group.getGroupDate())
            .createdAt(group.getCreatedAt())
            .maxPeople(group.getMaxPeople())
            .nowPeople(group.getNowPeople())
            .status(group.getStatus())
            .during(group.getDuring())
            .category(group.getCategory())
            .activated(group.getActivated())
            .build();
    }
}
