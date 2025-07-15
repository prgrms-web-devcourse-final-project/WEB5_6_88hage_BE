package com.grepp.funfun.app.domain.recommend.dto.payload;

import com.grepp.funfun.app.domain.group.dto.GroupWithReasonDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecommendGroupResponse {

    List<GroupWithReasonDTO> groups;

}
