package com.grepp.funfun.app.model.preference.dto;

import com.grepp.funfun.app.model.group.code.GroupClassification;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GroupPreferenceDTO {

    private Long id;

    private GroupClassification category;

    @NotNull
    @Size(max = 255)
    private String user;

}
