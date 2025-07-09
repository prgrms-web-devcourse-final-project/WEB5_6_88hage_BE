package com.grepp.funfun.app.domain.content.dto;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContentCategoryDTO {

    private Long id;

    private ContentClassification category;

}
