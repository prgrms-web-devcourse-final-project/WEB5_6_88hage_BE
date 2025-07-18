package com.grepp.funfun.app.domain.bookmark.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContentBookmarkDTO {

    private Long id;

    @Size(max = 255)
//    @NotNull
    private String email;

    @NotNull
    private Long contentId;

}
