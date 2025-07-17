package com.grepp.funfun.app.domain.user.dto.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoordinateResponse {
    private String email;

    private Double latitude;

    private Double longitude;
}
