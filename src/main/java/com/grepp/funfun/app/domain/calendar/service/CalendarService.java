package com.grepp.funfun.app.domain.calendar.service;

import com.grepp.funfun.app.domain.calendar.dto.payload.*;
import com.grepp.funfun.app.domain.calendar.entity.Calendar;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.notification.dto.NotificationDTO;
import com.grepp.funfun.app.domain.notification.service.NotificationService;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final ContentRepository contentRepository;
    private final NotificationService notificationService;

    @Transactional
    public void addContentCalendar(String email, CalendarContentRequest request) {
        Content content = contentRepository.findById(request.getActivityId())
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        // 중복 등록 방지
        boolean exists = calendarRepository.existsByEmailAndContentIdAndSelectedDate(
            email, content.getId(), request.getSelectedDate());

        if (exists) {
            throw new CommonException(ResponseCode.ALREADY_EXISTS, "이미 같은 날짜에 등록된 일정입니다.");
        }

        // bookmarkCount++;
        content.increaseBookmark();

        calendarRepository.save(
            Calendar.builder()
            .email(email)
            .type(ActivityType.CONTENT)
            .content(content)
            .selectedDate(request.getSelectedDate())
            .build()
        );
    }

    @Transactional
    public void deleteContentCalendar(Long calendarId, String email) {
        Calendar calendar = calendarRepository.findByIdAndEmail(calendarId, email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if(calendar.getType() == ActivityType.GROUP) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "모임 일정은 직접 삭제할 수 없습니다.");
        }

        // bookmarkCount--;
        Content content = calendar.getContent();
        if (content != null && content.getBookmarkCount() > 0) {
            content.decreaseBookmark();
        }

        calendarRepository.delete(calendar);
    }

    @Transactional
    public void updateContentCalendar(Long calendarId, LocalDateTime selectedDate, String email) {
        Calendar calendar = calendarRepository.findByIdAndEmail(calendarId, email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        calendar.updateSelectedDateForContent(selectedDate);
    }

    @Transactional(readOnly = true)
    public List<CalendarMonthlyResponse> getMonthly(String email, YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        return Stream.concat(
                calendarRepository.findMonthlyContentCalendars(email, start, end).stream(),
                calendarRepository.findMonthlyGroupCalendars(email, start, end).stream()
            )
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CalendarDailyResponse> getDaily(String email, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        return Stream.concat(
                calendarRepository.findDailyContentCalendars(email, start, end).stream(),
                calendarRepository.findDailyGroupCalendars(email, start, end).stream()
            )
            .sorted(Comparator.comparing(CalendarDailyResponse::getSelectedDate))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CalendarDailyResponse> getDailyForContent(String email, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        return calendarRepository.findDailyContentCalendars(email, start, end);
    }

    @Transactional(readOnly = true)
    public Page<CalendarContentResponse> getContent(String email, boolean pastIncluded, Pageable pageable) {
        return calendarRepository.findContentByEmail(email, pastIncluded, pageable);
    }

    @Transactional
    public void addGroupCalendar(String email, Group group) {
        calendarRepository.save(
            Calendar.builder()
            .email(email)
            .type(ActivityType.GROUP)
            .group(group)
            .build()
        );
    }

    @Transactional
    public void deleteGroupCalendar(Long groupId) {
        // 전체 삭제 (모임 자체가 삭제될 때)
        calendarRepository.deleteByGroupId(groupId);
    }

    @Transactional
    public void deleteGroupCalendarForUser(String email, Long groupId) {
        // 특정 유저만 삭제 (모임 나가기, 강퇴)
        calendarRepository.deleteByEmailAndGroupId(email, groupId);
    }

    public void createSchedule(CalendarScheduleRequest request) {
        Calendar calendar = calendarRepository.save(Calendar.builder()
                .email(request.getEmail())
                .selectedDate(request.getStartDate())
                .type(request.getType())
                .build());

        if (!notificationService.existsScheduleNotification(calendar.getId())) {
            NotificationDTO notification = NotificationDTO.builder()
                    .email(request.getEmail())
                    .link("/calendar/" + calendar.getId())
                    .type("SCHEDULE")
                    .isRead(false)
                    .scheduledAt(request.getStartDate().toLocalDate().atStartOfDay())
                    .calendarId(calendar.getId())
                    .build();

            notificationService.create(notification);
        }
    }

    public void updateSchedule(Long id, CalendarScheduleRequest request) {
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        calendar.updateSelectedDateForContent(request.getStartDate());
        calendarRepository.save(calendar);

        notificationService.deleteByCalendarId(id);

        NotificationDTO notification = NotificationDTO.builder()
                .email(request.getEmail())
                .link("/calendar/" + calendar.getId())
                .type("SCHEDULE")
                .isRead(false)
                .scheduledAt(request.getStartDate().toLocalDate().atStartOfDay())
                .calendarId(calendar.getId())
                .build();

        notificationService.create(notification);
    }

    public void deleteSchedule(Long id) {
        calendarRepository.deleteById(id);
        notificationService.deleteByCalendarId(id);
    }
}
