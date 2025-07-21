package com.grepp.funfun.app.domain.admin.dto.payload;

import lombok.Data;

@Data
public class AdminReportProcessRequest {
    private boolean takeAction;
    private int suspendDays;
    private String adminComment;
}
