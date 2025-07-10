package com.grepp.funfun.app.domain.preference.dto.payload;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Data;

@Data
public class PreferenceRequest {
    @NotEmpty(message = "최소 1개 이상의 컨텐츠 취향을 선택해주세요.")
    private Set<ContentClassification> contentPreferences;

    @NotEmpty(message = "최소 1개 이상의 모임 취향을 선택해주세요.")
    private Set<GroupClassification> groupPreferences;
}
