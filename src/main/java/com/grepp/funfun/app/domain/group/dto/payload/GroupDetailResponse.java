package com.grepp.funfun.app.domain.group.dto.payload;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.GroupHashtag;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupDetailResponse {

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

    private Double latitude;

    private Double longitude;

    private Integer during;

    private GroupClassification category;

    private String leaderNickname;

    private String leaderEmail;

    private String leaderExplain;

    private String leaderImgUrl;

    private List<GroupClassification> leaderHashTags;

    private List<String> hashTags;

    private Boolean activated;

    private List<GroupDetailResponse> relatedGroups;

    public static GroupDetailResponse fromWithRelated(Group group, List<GroupDetailResponse> relatedGroups) {
        return GroupDetailResponse.builder()
            .id(group.getId())
            .title(group.getTitle())
            .explain(group.getExplain())
            .simpleExplain(group.getSimpleExplain())
            .imageUrl(group.getImageUrl())
            .placeName(group.getPlaceName())
            .address(group.getAddress())
            .groupDate(group.getGroupDate())
            .createdAt(group.getCreatedAt())
            .viewCount(group.getViewCount())
            .maxPeople(group.getMaxPeople())
            .nowPeople(group.getNowPeople())
            .status(group.getStatus())
            .latitude(group.getLatitude())
            .longitude(group.getLongitude())
            .during(group.getDuring())
            .category(group.getCategory())
            .activated(group.getActivated())
            .leaderNickname(group.getLeader().getNickname())
            .leaderEmail(group.getLeader().getEmail())
            .leaderExplain(group.getLeader().getInfo().getIntroduction())
            .leaderImgUrl(group.getLeader().getInfo().getImageUrl())
            .leaderHashTags(group.getLeader().getGroupPreferences().stream()
                .map(GroupPreference::getCategory)
                .collect(Collectors.toList()))
            .hashTags(group.getHashtags().stream()
                .map(GroupHashtag::getTag)
                .collect(Collectors.toList()))
            .relatedGroups(relatedGroups)
            .build();
    }

}
