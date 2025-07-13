package com.grepp.funfun.app.domain.report.dto.payload;

import com.grepp.funfun.app.domain.report.vo.ReportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {

    @NotBlank(message = "신고자 이메일은 필수입니다.")
    private String reportingUserEmail;

    @NotBlank(message = "신고 대상자 이메일은 필수입니다.")
    private String reportedUserEmail;

    @NotBlank(message = "신고 사유는 필수입니다.")
    private String reason;

    @NotNull(message = "신고 타입(CHAT, POST)은 필수입니다.")
    private ReportType reportType;

    @NotNull(message = "신고 대상(채팅, 게시글)의 ID는 필수입니다.")
    private Long targetId;
}