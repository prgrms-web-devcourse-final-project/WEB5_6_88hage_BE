package com.grepp.funfun.app.domain.calendar.service;

import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarContentRequest;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarDailyResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarMonthlyResponse;
import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import com.grepp.funfun.app.domain.calendar.entity.Calendar;
import com.grepp.funfun.app.domain.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private ContentRepository contentRepository;

    private String email;
    private Long calendarId;

    @BeforeEach
    void setUp() {
        email = "user@example.com";
        calendarId = 1L;
    }

    @Test
    void addContentCalendar_OK() {
        // given
        Long contentId = 1L;
        LocalDateTime selectedDate = LocalDateTime.of(2025, 7, 8, 10, 0);
        Content content = new Content();
        CalendarContentRequest request = new CalendarContentRequest();
        request.setActivityId(contentId);
        request.setSelectedDate(selectedDate);

        // when
        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
        calendarService.addContentCalendar(email, request);

        // then
        verify(calendarRepository).save(any(Calendar.class));
    }

    @Test
    void addContentCalendar_NOT_FOUND_EX() {
        // given
        Long contentId = 1L;
        LocalDateTime selectedDate = LocalDateTime.of(2025, 7, 8, 10, 0);
        CalendarContentRequest request = new CalendarContentRequest();
        request.setActivityId(contentId);
        request.setSelectedDate(selectedDate);

        // when, then
        when(contentRepository.findById(contentId)).thenReturn(Optional.empty());
        assertThrows(CommonException.class, () -> calendarService.addContentCalendar(email, request));
    }

    @Test
    void deleteContentCalendar_OK() {
        // given
        Calendar calendar = Calendar.builder()
            .id(calendarId)
            .type(ActivityType.CONTENT)
            .build();

        // when
        when(calendarRepository.findByIdAndEmail(calendarId, email)).thenReturn(Optional.of(calendar));
        calendarService.deleteContentCalendar(calendarId, email);

        // then
        verify(calendarRepository).delete(calendar);
    }

    @Test
    void deleteContentCalendar_GROUP_TYPE_EX() {
        // given
        // GROUP 일 때
        Calendar calendar = Calendar.builder()
            .id(calendarId)
            .type(ActivityType.GROUP)
            .build();

        // when, then
        when(calendarRepository.findByIdAndEmail(calendarId, email)).thenReturn(Optional.of(calendar));
        assertThrows(CommonException.class, () -> calendarService.deleteContentCalendar(calendarId, email));
    }

    @Test
    void deleteContentCalendar_NOT_FOUND_EX() {
        // given

        // when, then
        when(calendarRepository.findByIdAndEmail(calendarId, email)).thenReturn(Optional.empty());
        assertThrows(CommonException.class, () -> calendarService.deleteContentCalendar(calendarId, email));
    }

    @Test
    void updateContentCalendar_OK() {
        // given
        LocalDateTime newDate = LocalDateTime.of(2025, 7, 8, 12, 0);
        Calendar calendar = Calendar.builder()
            .id(calendarId)
            .type(ActivityType.CONTENT)
            .build();

        // when
        when(calendarRepository.findByIdAndEmail(calendarId, email)).thenReturn(Optional.of(calendar));
        calendarService.updateContentCalendar(calendarId, newDate, email);

        // then
        assertEquals(newDate, calendar.getSelectedDate());
    }

    @Test
    void updateContentCalendar_GROUP_TYPE_EX() {
        // given
        LocalDateTime newDate = LocalDateTime.of(2025, 7, 8, 12, 0);
        // GROUP 일 때
        Calendar calendar = Calendar.builder()
            .id(calendarId)
            .type(ActivityType.GROUP)
            .build();

        // when, then
        when(calendarRepository.findByIdAndEmail(calendarId, email)).thenReturn(Optional.of(calendar));
        assertThrows(CommonException.class, () -> calendarService.updateContentCalendar(calendarId, newDate, email));
    }

    @Test
    void updateContentCalendar_NOT_FOUND_EX() {
        // given
        LocalDateTime newDate = LocalDateTime.of(2025, 7, 8, 12, 0);

        // when, then
        when(calendarRepository.findByIdAndEmail(calendarId, email)).thenReturn(Optional.empty());
        assertThrows(CommonException.class, () -> calendarService.updateContentCalendar(calendarId, newDate, email));
    }

    @Test
    void addGroupCalendar_OK() {
        // given
        Group group = new Group();

        // when
        calendarService.addGroupCalendar(email, group);

        // then
        verify(calendarRepository).save(any(Calendar.class));
    }

    @Test
    void deleteGroupCalendar_OK() {
        // given
        Long groupId = 1L;

        // when
        calendarService.deleteGroupCalendar(groupId);

        // then
        verify(calendarRepository).deleteByGroupId(groupId);
    }

    @Test
    void deleteGroupCalendarForUser_OK() {
        // given
        Long groupId = 1L;

        // when
        calendarService.deleteGroupCalendarForUser(email, groupId);

        // then
        verify(calendarRepository).deleteByEmailAndGroupId(email, groupId);
    }

    @Test
    void getMonthly_OK() {
        // given
        YearMonth month = YearMonth.of(2025, 7);

        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        CalendarMonthlyResponse content1 = CalendarMonthlyResponse.builder()
            .calendarId(1L)
            .type(ActivityType.CONTENT)
            .activityId(1L)
            .title("축제")
            .selectedDate(LocalDateTime.of(2025, 7, 5, 10, 0))
            .address("주소1")
            .build();
        CalendarMonthlyResponse group1 = CalendarMonthlyResponse.builder()
            .calendarId(2L)
            .type(ActivityType.GROUP)
            .activityId(1L)
            .title("모임")
            .selectedDate(LocalDateTime.of(2025, 7, 12, 15, 0))
            .address("주소2")
            .build();

        // when
        when(calendarRepository.findMonthlyContentCalendars(email, start, end))
            .thenReturn(List.of(content1));
        when(calendarRepository.findMonthlyGroupCalendars(email, start, end))
            .thenReturn(List.of(group1));
        List<CalendarMonthlyResponse> result = calendarService.getMonthly(email, month);

        // then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getTitle().equals("축제")));
        assertTrue(result.stream().anyMatch(r -> r.getTitle().equals("모임")));
    }

    @Test
    void getDaily_OK() {
        // given
        LocalDate date = LocalDate.of(2025, 7, 8);
        LocalDateTime date1 = date.atTime(10, 0);
        LocalDateTime date2 = date.atTime(9, 0);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        CalendarDailyResponse contentList = new CalendarDailyResponse(1L, ActivityType.CONTENT, 1L, "축제", date1, "축제 주소");
        CalendarDailyResponse groupList = new CalendarDailyResponse(2L, ActivityType.GROUP, 1L, "모임", date2, "모임 주소");

        // when
        when(calendarRepository.findDailyContentCalendars(email, start, end))
            .thenReturn(List.of(contentList));
        when(calendarRepository.findDailyGroupCalendars(email, start, end))
            .thenReturn(List.of(groupList));
        List<CalendarDailyResponse> result = calendarService.getDaily(email, date);

        // then
        assertEquals(2, result.size());
        assertEquals("모임", result.get(0).getTitle()); // 날짜 빠른 게 먼저
        assertEquals("축제", result.get(1).getTitle());
    }

    @Test
    void getDailyForContent_OK() {
        // given
        LocalDate localDate = LocalDate.of(2025, 7, 8);
        LocalDateTime localDateTime = localDate.atTime(9, 0);

        LocalDateTime start = localDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        CalendarDailyResponse contentList = new CalendarDailyResponse(1L, ActivityType.CONTENT, 1L, "축제", localDateTime, "축제 주소");

        // when
        when(calendarRepository.findDailyContentCalendars(email, start, end))
            .thenReturn(List.of(contentList));
        List<CalendarDailyResponse> result = calendarService.getDailyForContent(email, localDate);

        // then
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getTitle().equals("축제")));
    }
}