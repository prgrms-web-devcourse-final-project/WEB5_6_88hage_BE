package com.grepp.funfun.app.domain.recommend.dto.payload;

import com.grepp.funfun.app.domain.content.dto.ContentWithReasonDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecommendTwoListResponse {
    private List<ContentWithReasonDTO> events;
    private List<ContentWithReasonDTO> places;
}
