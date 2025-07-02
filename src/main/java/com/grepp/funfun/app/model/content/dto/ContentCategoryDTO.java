package com.grepp.funfun.app.model.content.dto;

import com.grepp.funfun.app.model.content.code.ContentClassification;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContentCategoryDTO {

    private Long id;
    private ContentClassification category;
    private Integer during;

}
