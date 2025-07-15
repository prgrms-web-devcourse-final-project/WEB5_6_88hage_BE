package com.grepp.funfun.app.domain.recommend.dto.payload;


import com.grepp.funfun.app.domain.recommend.vo.EventType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RecommendRequest {

    private EventType eventType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String address;
}
