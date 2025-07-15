package com.grepp.funfun.app.domain.admin.service;

import com.grepp.funfun.app.domain.admin.dto.AdminReportViewDTO;
import com.grepp.funfun.app.domain.admin.dto.payload.AdminReportProcessRequest;
import com.grepp.funfun.app.domain.admin.vo.ReportSourceType;
import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.repository.ContactRepository;
import com.grepp.funfun.app.domain.contact.vo.ContactCategory;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.domain.report.entity.Report;
import com.grepp.funfun.app.domain.report.repository.ReportRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;
    private final ContactRepository contactRepository;
    private final AdminUserService adminUserService;

    public List<AdminReportViewDTO> getAllReports(String status) {
        List<AdminReportViewDTO> combined = new ArrayList<>();

        List<Report> reports = reportRepository.findAll();
        List<Contact> contacts = contactRepository.findAll().stream()
                .filter(c -> c.getCategory() == ContactCategory.REPORT)
                .toList();

        if(status.equalsIgnoreCase("resolved")) {
            reports = reports.stream().filter(Report::isResolved).toList();
            contacts = contacts.stream().filter(c -> c.getStatus() == ContactStatus.COMPLETE).toList();
        } else if (status.equalsIgnoreCase("unresolved")) {
            reports = reports.stream().filter(r -> !r.isResolved()).toList();
            contacts = contacts.stream().filter(c -> c.getStatus() == ContactStatus.PENDING).toList();
        }

        for(Report r : reports) {
            AdminReportViewDTO dto = new AdminReportViewDTO();
            dto.setId(r.getId());
            dto.setReportingUserEmail(r.getReportingUser().getEmail());
            dto.setReportedUserEmail(r.getReportedUser().getEmail());
            dto.setReason(r.getReason());
            dto.setSourceType(ReportSourceType.BUTTON_REPORT);
            dto.setResolved(r.isResolved());
            dto.setAdminComment(r.getAdminComment());
            dto.setReportedAt(r.getCreatedAt());
            dto.setResolvedAt(r.getResolvedAt());
            combined.add(dto);
        }

        for (Contact c : contacts) {
            AdminReportViewDTO dto = new AdminReportViewDTO();
            dto.setId(c.getId());
            dto.setReportingUserEmail(c.getUser().getEmail());
            dto.setReportedUserEmail(null);
            dto.setReason(c.getContent());
            dto.setSourceType(ReportSourceType.CONTACT_REPORT);
            dto.setResolved(c.getStatus() == ContactStatus.COMPLETE);
            dto.setAdminComment(c.getAnswer());
            dto.setReportedAt(c.getCreatedAt());
            dto.setResolvedAt(c.getAnsweredAt());
            combined.add(dto);
        }

        combined.sort(Comparator.comparing(AdminReportViewDTO::getReportedAt).reversed());

        return combined;
    }

    @Transactional
    public void processReport(Long id, AdminReportProcessRequest request) {
        Optional<Report> optionalReport = reportRepository.findById(id);
        if (optionalReport.isPresent()) {
            Report report = optionalReport.get();

            if(report.isResolved()) {
                throw new CommonException(ResponseCode.BAD_REQUEST, "이미 처리 완료된 신고입니다.");
            }

            if(request.isTakeAction()) {
                adminUserService.suspendUser(
                        report.getReportedUser().getEmail(),
                        request.getSuspendDays(),
                        request.getAdminComment()
                );
            }

            report.setResolved(true);
            report.setAdminComment(request.getAdminComment());
            report.setResolvedAt(LocalDateTime.now());
            return;
        }

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "신고(ID=" + id + ")를 찾을 수 없습니다."));

                if (contact.getCategory() != ContactCategory.REPORT) {
                    throw new CommonException(ResponseCode.BAD_REQUEST, "신고 유형의 문의가 아닙니다.");
                }

                if (contact.getStatus() == ContactStatus.COMPLETE) {
                    throw new CommonException(ResponseCode.BAD_REQUEST, "이미 처리 완료된 신고입니다.");
                }

                contact.setAnswer(request.getAdminComment());
                contact.setStatus(ContactStatus.COMPLETE);
                contact.setAnsweredAt(LocalDateTime.now());
    }
}
