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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ChatRepository chatRepository;

    private String reportingUserEmail;
    private String reportedUserEmail;
    private User reportingUser;
    private User reportedUser;

    @BeforeEach
    void setUp() {
        reportingUserEmail = "test@test.test";
        reportedUserEmail = "target@target.target";
        reportingUser = User.builder().email(reportingUserEmail).build();
        reportedUser = User.builder().email(reportedUserEmail).build();
    }

    @Test
    void create_POST_OK() {
        // given
        ReportRequest reportRequest = ReportRequest.builder()
            .reportedUserEmail(reportedUserEmail)
            .reason("부적절한 게시글")
            .reportType(ReportType.POST)
            .targetId(1L)
            .build();

        Group group = Group.builder()
            .id(1L)
            .leader(reportedUser)
            .build();

        // when
        when(userRepository.findById(reportingUserEmail)).thenReturn(Optional.of(reportingUser));
        when(userRepository.findById(reportedUserEmail)).thenReturn(Optional.of(reportedUser));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        reportService.create(reportingUserEmail, reportRequest);

        // then
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void create_NOT_LEADER_EX() {
        // given
        ReportRequest reportRequest = ReportRequest.builder()
            .reportedUserEmail(reportedUserEmail)
            .reason("부적절한 게시글")
            .reportType(ReportType.POST)
            .targetId(1L)
            .build();

        Group group = Group.builder()
            .id(1L)
            .leader(User.builder().email("other@other.other").build())
            .build();

        // when
        when(userRepository.findById(reportingUserEmail)).thenReturn(Optional.of(reportingUser));
        when(userRepository.findById(reportedUserEmail)).thenReturn(Optional.of(reportedUser));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        // then
        assertThrows(CommonException.class, () ->  reportService.create(reportingUserEmail, reportRequest));
    }

    @Test
    void create_CHAT_OK() {
        // given
        ReportRequest reportRequest = ReportRequest.builder()
            .reportedUserEmail(reportedUserEmail)
            .reason("부적절한 채팅")
            .reportType(ReportType.CHAT)
            .targetId(1L)
            .build();

        Chat chat = Chat.builder()
//            .id(1L)
            .senderEmail(reportedUserEmail)
            .build();

        // when
        when(userRepository.findById(reportingUserEmail)).thenReturn(Optional.of(reportingUser));
        when(userRepository.findById(reportedUserEmail)).thenReturn(Optional.of(reportedUser));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        reportService.create(reportingUserEmail, reportRequest);

        // then
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void create_NOT_SENDER_EX() {
        // given
        ReportRequest reportRequest = ReportRequest.builder()
            .reportedUserEmail(reportedUserEmail)
            .reason("부적절한 채팅")
            .reportType(ReportType.CHAT)
            .targetId(1L)
            .build();

        Chat chat = Chat.builder()
//            .id(1L)
            .senderEmail("other@other.other")
            .build();

        // when
        when(userRepository.findById(reportingUserEmail)).thenReturn(Optional.of(reportingUser));
        when(userRepository.findById(reportedUserEmail)).thenReturn(Optional.of(reportedUser));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));

        // then
        assertThrows(CommonException.class, () ->  reportService.create(reportingUserEmail, reportRequest));
    }

    @Test
    void create_SELF_REPORT_OK() {
        // given
        ReportRequest reportRequest = ReportRequest.builder()
            .reportedUserEmail(reportingUserEmail)
            .build();

        // when, then
        assertThrows(CommonException.class, () -> reportService.create(reportingUserEmail, reportRequest));
    }
}