package com.grepp.funfun.app.domain.content.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentUrlDTO {

    private Long id;
    private String siteName;
    private String url;

}
