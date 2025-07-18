package com.grepp.funfun.app.domain.content.dto.payload;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ContentFilterRequest {

    private ContentClassification category;
    private String guName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

}
