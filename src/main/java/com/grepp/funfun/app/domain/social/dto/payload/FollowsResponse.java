package com.grepp.funfun.app.domain.social.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FollowsResponse {
    String email;
    String nickname;
    String imageUrl;
}
