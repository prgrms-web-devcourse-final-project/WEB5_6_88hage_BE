package com.grepp.funfun.app.domain.user.dto.payload;

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
    private List<String> hashtags;
    private long followerCount;
    private long followingCount;
    private long groupLeadCount;
    private long groupJoinCount;
}
