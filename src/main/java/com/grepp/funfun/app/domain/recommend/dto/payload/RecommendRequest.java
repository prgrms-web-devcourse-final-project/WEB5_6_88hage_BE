package com.grepp.funfun.app.domain.recommend.dto.payload;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "추천 요청 정보")
public class RecommendRequest {

    @NotNull
    @Schema(description = "시작 시간", example = "2025-08-03T18:00:00", required = true)
    private LocalDateTime startTime;

    @NotNull
    @Schema(description = "종료 시간", example = "2025-08-05T18:00:00", required = true)
    private LocalDateTime endTime;

    @Schema(description = "주소", example = "서울특별시 강남구 역삼동")
    private String address;
}
