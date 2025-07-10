package com.grepp.funfun.app.domain.preference.dto.payload;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Data;

@Data
public class GroupPreferenceRequest {
    @NotEmpty(message = "최소 1개 이상의 취향을 선택해주세요.")
    private Set<GroupClassification> preferences;
}
