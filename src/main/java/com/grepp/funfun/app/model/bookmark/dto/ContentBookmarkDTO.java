package com.grepp.funfun.app.model.bookmark.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContentBookmarkDTO {

    private Long id;

    @Size(max = 255)
    private String email;

    @NotNull
    private Long content;

}
