package com.grepp.funfun.app.domain.preference.dto.payload;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PreferenceResponse {
    private Set<ContentClassification> contentPreferences;
    private Set<GroupClassification> groupPreferences;
}
