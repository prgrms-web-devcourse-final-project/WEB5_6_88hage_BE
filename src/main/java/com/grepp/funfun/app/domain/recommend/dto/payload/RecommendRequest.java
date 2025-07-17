package com.grepp.funfun.app.domain.recommend.dto.payload;


import com.grepp.funfun.app.domain.recommend.vo.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "추천 요청 정보")
public class RecommendRequest {

    @Schema(description = "이벤트 타입", example = "CONTENT", required = true)
    private EventType eventType;

    @Schema(description = "시작 시간", example = "2025-07-29T18:00:00", required = true)
    private LocalDateTime startTime;

    @Schema(description = "종료 시간", example = "2024-07-30T18:00:00", required = true)
    private LocalDateTime endTime;

    @Schema(description = "주소", example = "서울시 강남구 역삼동")
    private String address;
}
