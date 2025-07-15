package com.grepp.funfun.app.domain.user.dto.payload;

import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailResponse {
    private String email;
    private String nickname;
    private String introduction;
    private String imageUrl;
    private List<GroupClassification> groupPreferences;
    private List<ContentPreference> contentPreferences;
    private long followerCount;
    private long followingCount;
    private long groupLeadCount;
    private long groupJoinCount;
}
