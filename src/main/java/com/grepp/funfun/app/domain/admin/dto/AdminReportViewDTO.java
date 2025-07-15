package com.grepp.funfun.app.domain.admin.dto;

import com.grepp.funfun.app.domain.admin.vo.ReportSourceType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminReportViewDTO {
    private Long id;

    private String reportingUserEmail;
    private String reportedUserEmail; // 문의 신고는 null 으로 지정

    private String reason; // Report.reason && Contact.content
    private ReportSourceType sourceType; // 신고 경로 button or contact

    private boolean resolved;
    private String adminComment; // Report.adminComment && Contact.answer
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
}
