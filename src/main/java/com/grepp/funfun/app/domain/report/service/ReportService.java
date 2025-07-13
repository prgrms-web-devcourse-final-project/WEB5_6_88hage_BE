package com.grepp.funfun.app.domain.report.service;

import com.grepp.funfun.app.domain.report.dto.ReportDTO;
import com.grepp.funfun.app.domain.report.dto.payload.ReportRequest;
import com.grepp.funfun.app.domain.report.entity.Report;
import com.grepp.funfun.app.domain.report.repository.ReportRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    @Transactional
    public void create(ReportRequest reportRequest) {
        if (reportRequest.getReportingUserEmail().equals(reportRequest.getReportedUserEmail())) {
            // 자기 자신은 신고할 수 없습니다.
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        User reportingUser = getUser(reportRequest.getReportingUserEmail());
        User reportedUser = getUser(reportRequest.getReportedUserEmail());

        Report report = Report.builder()
            .reportingUser(reportingUser)
            .reportedUser(reportedUser)
            .reason(reportRequest.getReason())
            .type(reportRequest.getReportType())
            .targetId(reportRequest.getTargetId())
            .build();
        reportRepository.save(report);
    }

    public List<ReportDTO> findAll() {
        final List<Report> reports = reportRepository.findAll(Sort.by("id"));
        return reports.stream()
                .map(report -> mapToDTO(report, new ReportDTO()))
                .toList();
    }

    public ReportDTO get(final Long id) {
        return reportRepository.findById(id)
                .map(report -> mapToDTO(report, new ReportDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ReportDTO reportDTO) {
        final Report report = new Report();
        mapToEntity(reportDTO, report);
        return reportRepository.save(report).getId();
    }

    public void update(final Long id, final ReportDTO reportDTO) {
        final Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(reportDTO, report);
        reportRepository.save(report);
    }

    public void delete(final Long id) {
        reportRepository.deleteById(id);
    }

    private ReportDTO mapToDTO(final Report report, final ReportDTO reportDTO) {
        reportDTO.setId(report.getId());
        reportDTO.setReason(report.getReason());
        reportDTO.setType(report.getType());
        reportDTO.setTargetId(report.getTargetId());
        reportDTO.setReportingUser(report.getReportingUser() == null ? null : report.getReportingUser().getEmail());
        reportDTO.setReportedUser(report.getReportedUser() == null ? null : report.getReportedUser().getEmail());
        return reportDTO;
    }

    private Report mapToEntity(final ReportDTO reportDTO, final Report report) {
        report.setReason(reportDTO.getReason());
        report.setType(reportDTO.getType());
        report.setTargetId(reportDTO.getTargetId());
        final User reportingUser = reportDTO.getReportingUser() == null ? null : userRepository.findById(reportDTO.getReportingUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        report.setReportingUser(reportingUser);
        final User reportedUser = reportDTO.getReportedUser() == null ? null : userRepository.findById(reportDTO.getReportedUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        report.setReportedUser(reportedUser);
        return report;
    }

}
