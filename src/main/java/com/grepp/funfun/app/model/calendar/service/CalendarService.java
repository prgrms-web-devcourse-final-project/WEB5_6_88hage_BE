package com.grepp.funfun.app.model.calendar.service;

import com.grepp.funfun.app.controller.api.calendar.payload.CalendarContentRequest;
import com.grepp.funfun.app.controller.api.calendar.payload.CalendarDailyResponse;
import com.grepp.funfun.app.controller.api.calendar.payload.CalendarMonthlyResponse;
import com.grepp.funfun.app.model.calendar.code.ActivityType;
import com.grepp.funfun.app.model.calendar.dto.CalendarDTO;
import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final ContentRepository contentRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void addContentCalendar(String email, CalendarContentRequest request) {
        Calendar calendar = new Calendar();
        calendar.setEmail(email);
        calendar.setType(request.getType());

        if(calendar.getType() == ActivityType.GROUP) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        Content content = contentRepository.findById(request.getActivityId())
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        calendar.setContent(content);
        calendar.setSelectedDate(request.getSelectedDate());

        calendarRepository.save(calendar);
    }

    @Transactional
    public void deleteContentCalendar(Long calendarId, String email) {
        Calendar calendar = calendarRepository.findByIdAndEmail(calendarId, email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if(calendar.getType() == ActivityType.GROUP) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        calendarRepository.delete(calendar);
    }

    @Transactional
    public void updateContentCalendar(Long calendarId, LocalDateTime selectedDate, String email) {
        Calendar calendar = calendarRepository.findByIdAndEmail(calendarId, email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        if (calendar.getType() == ActivityType.GROUP) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        calendar.setSelectedDate(selectedDate);
    }

    public List<CalendarMonthlyResponse> getMonthly(String email, YearMonth month) {
        LocalDateTime start = month.atDay(1).atStartOfDay();
        LocalDateTime end = month.atEndOfMonth().atTime(LocalTime.MAX);

        List<CalendarMonthlyResponse> contentList = calendarRepository.findMonthlyContentCalendars(email, start, end);
        List<CalendarMonthlyResponse> groupList = calendarRepository.findMonthlyGroupCalendars(email, start, end);

        List<CalendarMonthlyResponse> result = new ArrayList<>();
        result.addAll(contentList);
        result.addAll(groupList);

        return result;
    }

    public List<CalendarDailyResponse> getDaily(String email, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1).minusNanos(1);

        List<CalendarDailyResponse> content = calendarRepository.findDailyContentCalendars(email, start, end);
        List<CalendarDailyResponse> group = calendarRepository.findDailyGroupCalendars(email, start, end);

        List<CalendarDailyResponse> result = new ArrayList<>();
        result.addAll(content);
        result.addAll(group);
        // 오름차순 정렬
        result.sort(Comparator.comparing(CalendarDailyResponse::getSelectedDate));

        return result;
    }

    @Transactional
    public void addGroupCalendar(String email, Group group) {
        Calendar calendar = new Calendar();
        calendar.setEmail(email);
        calendar.setType(ActivityType.GROUP);
        calendar.setGroup(group);
        calendarRepository.save(calendar);
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

    public List<CalendarDTO> findAll() {
        final List<Calendar> calendars = calendarRepository.findAll(Sort.by("id"));
        return calendars.stream()
                .map(calendar -> mapToDTO(calendar, new CalendarDTO()))
                .toList();
    }

    public CalendarDTO get(final Long id) {
        return calendarRepository.findById(id)
                .map(calendar -> mapToDTO(calendar, new CalendarDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final CalendarDTO calendarDTO) {
        final Calendar calendar = new Calendar();
        mapToEntity(calendarDTO, calendar);
        return calendarRepository.save(calendar).getId();
    }

    public void update(final Long id, final CalendarDTO calendarDTO) {
        final Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(calendarDTO, calendar);
        calendarRepository.save(calendar);
    }

    public void delete(final Long id) {
        calendarRepository.deleteById(id);
    }

    private CalendarDTO mapToDTO(final Calendar calendar, final CalendarDTO calendarDTO) {
        calendarDTO.setId(calendar.getId());
        calendarDTO.setEmail(calendar.getEmail());
        calendarDTO.setSelectedDate(calendar.getSelectedDate());
        calendarDTO.setType(calendar.getType());
        calendarDTO.setContent(calendar.getContent() == null ? null : calendar.getContent().getId());
        calendarDTO.setGroup(calendar.getGroup() == null ? null : calendar.getGroup().getId());
        return calendarDTO;
    }

    private Calendar mapToEntity(final CalendarDTO calendarDTO, final Calendar calendar) {
        calendar.setEmail(calendarDTO.getEmail());
        calendar.setSelectedDate(calendarDTO.getSelectedDate());
        calendar.setType(calendarDTO.getType());
        final Content content = calendarDTO.getContent() == null ? null : contentRepository.findById(calendarDTO.getContent())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        calendar.setContent(content);
        final Group group = calendarDTO.getGroup() == null ? null : groupRepository.findById(calendarDTO.getGroup())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        calendar.setGroup(group);
        return calendar;
    }

}
