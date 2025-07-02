package com.grepp.funfun.app.model.calendar.service;

import com.grepp.funfun.app.model.calendar.dto.CalendarDTO;
import com.grepp.funfun.app.model.calendar.entity.Calendar;
import com.grepp.funfun.app.model.calendar.repository.CalendarRepository;
import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final ContentRepository contentRepository;
    private final GroupRepository groupRepository;

    public CalendarService(final CalendarRepository calendarRepository,
            final ContentRepository contentRepository, final GroupRepository groupRepository) {
        this.calendarRepository = calendarRepository;
        this.contentRepository = contentRepository;
        this.groupRepository = groupRepository;
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
