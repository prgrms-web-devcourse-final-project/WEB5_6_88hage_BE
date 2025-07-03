package com.grepp.funfun.app.model.group.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupHashtagDTO {

    private Long id;

    @Size(max = 255)
    private String tag;

    private Long group;

}
