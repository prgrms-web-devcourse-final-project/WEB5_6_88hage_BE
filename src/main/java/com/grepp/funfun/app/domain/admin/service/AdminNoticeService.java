package com.grepp.funfun.app.domain.admin.service;

import com.grepp.funfun.app.domain.admin.dto.AdminNoticeDTO;
import com.grepp.funfun.app.domain.admin.entity.AdminNotice;
import com.grepp.funfun.app.domain.admin.mapper.AdminNoticeDTOMapper;
import com.grepp.funfun.app.domain.admin.repository.AdminNoticeRepository;
import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.service.NotificationService;
import com.grepp.funfun.app.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final AdminNoticeDTOMapper mapper;
    private final AdminNoticeRepository repository;
    private final UserService userService;
    private final NotificationService notificationService;

    // 공지사항 저장 + 알림 일괄 전송
    public Long create(AdminNoticeDTO dto) {
        AdminNotice notice = mapper.toEntity(dto);
        AdminNotice saved = repository.save(notice);

        List<String> emails = userService.getAllUserEmails();

        for (String email : emails) {
            NotificationDTO notification = NotificationDTO.builder()
                    .email(email)
                    .message("[공지] " + saved.getTitle())
                    .link("/notices/" + saved.getId()) // FE 라우팅 경로에 따라 수정 필요
                    .type("NOTICE")
                    .isRead(false)
                    .scheduledAt(LocalDateTime.now())
                    .sentAt(LocalDateTime.now())
                    .build();

            notificationService.create(notification);
        }

        return saved.getId();
    }

    public Page<AdminNoticeDTO> findAll(Pageable pageable) {
        return repository.findAllByOrderByCreatedAtDesc(pageable)
                .map(mapper::toDTO);
    }

    public AdminNoticeDTO findById(Long id) {
        AdminNotice notice = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        return mapper.toDTO(notice);
    }
}
