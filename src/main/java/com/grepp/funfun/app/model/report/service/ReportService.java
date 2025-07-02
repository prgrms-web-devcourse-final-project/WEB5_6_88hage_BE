package com.grepp.funfun.app.model.report.service;

import com.grepp.funfun.app.model.report.dto.ReportDTO;
import com.grepp.funfun.app.model.report.entity.Report;
import com.grepp.funfun.app.model.report.repository.ReportRepository;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(final ReportRepository reportRepository,
            final UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
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
        reportDTO.setContentId(report.getContentId());
        reportDTO.setReportingUser(report.getReportingUser() == null ? null : report.getReportingUser().getEmail());
        reportDTO.setReportedUser(report.getReportedUser() == null ? null : report.getReportedUser().getEmail());
        return reportDTO;
    }

    private Report mapToEntity(final ReportDTO reportDTO, final Report report) {
        report.setReason(reportDTO.getReason());
        report.setType(reportDTO.getType());
        report.setContentId(reportDTO.getContentId());
        final User reportingUser = reportDTO.getReportingUser() == null ? null : userRepository.findById(reportDTO.getReportingUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        report.setReportingUser(reportingUser);
        final User reportedUser = reportDTO.getReportedUser() == null ? null : userRepository.findById(reportDTO.getReportedUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        report.setReportedUser(reportedUser);
        return report;
    }

}
