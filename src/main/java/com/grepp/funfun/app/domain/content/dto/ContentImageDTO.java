package com.grepp.funfun.app.domain.content.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentImageDTO {
    private Long id;
    private String imageUrl;
}
