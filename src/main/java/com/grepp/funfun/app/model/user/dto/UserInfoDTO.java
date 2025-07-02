package com.grepp.funfun.app.model.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserInfoDTO {

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String imageUrl;

    @Size(max = 255)
    private String introduction;

}
