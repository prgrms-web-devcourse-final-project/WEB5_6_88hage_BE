package com.grepp.funfun.app.domain.content.dto.payload;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ContentFilterRequest {

    @Schema(description = "카테고리", example = "THEATER")
    private ContentClassification category;

    @Schema(description = "지역구", example = "강남구")
    private String guname;

    @Schema(description = "시작일", example = "2025-07-01")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @Schema(description = "종료일", example = "2025-12-31")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

}
