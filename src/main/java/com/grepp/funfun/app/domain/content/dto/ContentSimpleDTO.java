package com.grepp.funfun.app.domain.content.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSimpleDTO {
    private Long id;

    private String contentTitle;

    private String guname;

    private String poster;

    @NotNull
    private String category;

    private Double latitude;

    private Double longitude;
}
