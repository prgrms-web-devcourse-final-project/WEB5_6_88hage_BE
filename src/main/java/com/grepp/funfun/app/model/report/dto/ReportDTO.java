package com.grepp.funfun.app.model.report.dto;

import com.grepp.funfun.app.model.report.code.ReportType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReportDTO {

    private Long id;

    @Size(max = 255)
    private String reason;

    private ReportType type;

    @Size(max = 255)
    private String contentId;

    @NotNull
    @Size(max = 255)
    private String reportingUser;

    @NotNull
    @Size(max = 255)
    private String reportedUser;

}
