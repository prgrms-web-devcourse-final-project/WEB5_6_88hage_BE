package com.grepp.funfun.app.domain.content.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentUrlDTO {

    private Long id;
    private String siteName;
    private String url;

}
