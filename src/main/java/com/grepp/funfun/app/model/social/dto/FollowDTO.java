package com.grepp.funfun.app.model.social.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FollowDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String follower;

    @NotNull
    @Size(max = 255)
    private String followee;

}
