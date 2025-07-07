package com.grepp.funfun.app.controller.api.social.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowsResponse {
    String email;
    String nickname;
    String imageUrl;
}
