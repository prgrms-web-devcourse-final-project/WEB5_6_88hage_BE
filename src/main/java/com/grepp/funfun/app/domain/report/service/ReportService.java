package com.grepp.funfun.app.domain.report.service;

import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.repository.ChatRepository;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.report.dto.payload.ReportRequest;
import com.grepp.funfun.app.domain.report.entity.Report;
import com.grepp.funfun.app.domain.report.repository.ReportRepository;
import com.grepp.funfun.app.domain.report.vo.ReportType;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ChatRepository chatRepository;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "존재하지 않는 사용자입니다: " + email));
    }

    @Transactional
    public void create(String reportingUserEmail, ReportRequest reportRequest) {
        if (reportingUserEmail.equals(reportRequest.getReportedUserEmail())) {
            // 자기 자신은 신고할 수 없습니다.
            throw new CommonException(ResponseCode.BAD_REQUEST, "자기 자신을 신고할 수 없습니다.");
        }
        User reportingUser = getUser(reportingUserEmail);
        User reportedUser = getUser(reportRequest.getReportedUserEmail());

        // === 신고 대상 리소스 검증 ===
        // 신고 타입이 모임 게시글일 때
        if (reportRequest.getReportType() == ReportType.POST) {
            // 존재하는 모임인지 확인
            Group group = groupRepository.findById(reportRequest.getTargetId())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "모임 게시글을 찾을 수 없습니다."));

            // 신고 대상자가 작성한 모임 게시글이 맞는지 확인
            if (!group.getLeader().getEmail().equals(reportRequest.getReportedUserEmail())) {
                throw new CommonException(ResponseCode.BAD_REQUEST, "신고 대상자가 해당 모임의 리더가 아닙니다.");
            }
        }

        // 신고 타입이 채팅일 때
        if (reportRequest.getReportType() == ReportType.CHAT) {
            // 존재하는 채팅인지 확인
            Chat chat = chatRepository.findById(reportRequest.getTargetId())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "채팅 메시지를 찾을 수 없습니다."));

            // 신고 대상자가 작성한 채팅이 맞는지 확인
            if (!chat.getSenderEmail().equals(reportRequest.getReportedUserEmail())) {
                throw new CommonException(ResponseCode.BAD_REQUEST, "신고 대상자가 해당 채팅의 작성자가 아닙니다.");
            }
        }

        Report report = Report.builder()
            .reportingUser(reportingUser)
            .reportedUser(reportedUser)
            .reason(reportRequest.getReason())
            .type(reportRequest.getReportType())
            .targetId(reportRequest.getTargetId())
            .build();
        reportRepository.save(report);
    }

}
