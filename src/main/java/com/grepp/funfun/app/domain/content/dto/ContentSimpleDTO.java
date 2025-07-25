package com.grepp.funfun.app.domain.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSimpleDTO {
    private Long id;
    private String contentTitle;
    private String guname;
    private String poster;
}
