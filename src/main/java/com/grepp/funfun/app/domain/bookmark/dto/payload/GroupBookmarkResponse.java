package com.grepp.funfun.app.domain.bookmark.dto.payload;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupBookmarkResponse {

    private Long groupId;
    private String groupTitle;
    private String groupSimpleExplain;
    private GroupClassification groupCategory;

    private String placeName;
    private String address;
    private LocalDateTime groupDate;

    private Integer maxPeople;
    private Integer nowPeople;

    private GroupStatus status;

    private String leaderEmail;

}
