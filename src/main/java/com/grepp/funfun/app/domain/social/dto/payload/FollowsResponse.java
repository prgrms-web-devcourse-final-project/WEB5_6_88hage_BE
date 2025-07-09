package com.grepp.funfun.app.domain.social.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowsResponse {
    String email;
    String nickname;
    String imageUrl;
}
