package com.grepp.funfun.app.domain.user.dto.payload;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailResponse {
    private String email;
    private String nickname;
    private String introduction;
    private String imageUrl;
    private Set<ContentClassification> contentPreferences;
    private Set<GroupClassification> groupPreferences;
    private long followerCount;
    private long followingCount;
}
