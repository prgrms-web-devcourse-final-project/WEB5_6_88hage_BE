package com.grepp.funfun.app.domain.participant.dto.payload;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupCompletedStatsResponse {
    private GroupClassification category;
    private long count;
}
