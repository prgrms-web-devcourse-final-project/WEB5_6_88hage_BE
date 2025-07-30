package com.grepp.funfun.app.domain.notification.scheduler;

import com.grepp.funfun.app.domain.calendar.entity.Calendar;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.service.NotificationService;
import com.grepp.funfun.app.domain.notification.vo.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@RequiredArgsConstructor
@Slf4j
@Component
public class NotificationScheduler {

    private final CalendarRepository calendarRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendCalendarReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end = tomorrow.atTime(23, 59, 59);

        List<Calendar> schedules = calendarRepository.findBySelectedDateBetween(start, end);
        log.info("[{}] 일정 알림 대상 수: {}", tomorrow, schedules.size());

        for (Calendar calendar : schedules) {

            if (notificationService.existsScheduleNotification(calendar.getId())) {
                continue;
            }

            String activityType = calendar.getType().name().equals("GROUP") ? "모임" : "콘텐츠";
            String message = String.format("오늘 [%s] 일정이 있습니다. 캘린더를 확인해보세요.", activityType);

            NotificationDTO dto = NotificationDTO.builder()
                    .email(calendar.getEmail())
                    .message(message)
                    .link("/user/calendar") //fe에서의 라우터에 따라 변경
                    .isRead(false)
                    .type(NotificationType.SCHEDULE.name())
                    .scheduledAt(LocalDateTime.now())
                    .sentAt(LocalDateTime.now())
                    .calendarId(calendar.getId())
                    .build();

            notificationService.create(dto);
        }
    }
}
