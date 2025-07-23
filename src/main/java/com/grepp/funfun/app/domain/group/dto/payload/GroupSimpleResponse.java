package com.grepp.funfun.app.domain.group.dto.payload;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GroupSimpleResponse {

    private Long groupId;
    private String groupTitle;
    private String explain;
    private String simpleExplain;
    private String groupImageUrl;
    private GroupStatus groupStatus;
    private GroupClassification category;

    public static GroupSimpleResponse toSimpleResponse(Group group) {
        return GroupSimpleResponse.builder()
            .groupId(group.getId())
            .groupTitle(group.getTitle())
            .explain(group.getExplain())
            .simpleExplain(group.getSimpleExplain())
            .groupImageUrl(group.getImageUrl())
            .groupStatus(group.getStatus())
            .category(group.getCategory())
            .build();
    }
}
