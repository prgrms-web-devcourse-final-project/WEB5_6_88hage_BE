package com.grepp.funfun.app.domain.social.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FollowsResponse {
    private String email;
    private String nickname;
    private String imageUrl;
    private boolean isMutual;
}
