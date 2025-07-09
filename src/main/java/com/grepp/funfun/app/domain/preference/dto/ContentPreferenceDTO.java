package com.grepp.funfun.app.domain.preference.dto;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContentPreferenceDTO {

    private Long id;

    private ContentClassification category;

    @NotNull
    @Size(max = 255)
    private String user;

}
