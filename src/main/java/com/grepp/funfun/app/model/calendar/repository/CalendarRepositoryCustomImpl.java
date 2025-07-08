package com.grepp.funfun.app.model.calendar.repository;

import com.grepp.funfun.app.controller.api.calendar.payload.CalendarMonthlyResponse;
import com.grepp.funfun.app.model.calendar.code.ActivityType;
import com.grepp.funfun.app.model.calendar.entity.QCalendar;
import com.grepp.funfun.app.model.content.entity.QContent;
import com.grepp.funfun.app.model.group.entity.QGroup;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CalendarRepositoryCustomImpl implements CalendarRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QCalendar calendar = QCalendar.calendar;
    private final QContent content = QContent.content;
    private final QGroup group = QGroup.group;

    @Override
    public List<CalendarMonthlyResponse> findMonthlyContentCalendars(String email, LocalDateTime start,
        LocalDateTime end) {
        return queryFactory
            .select(Projections.constructor(
                CalendarMonthlyResponse.class,
                calendar.id,
                calendar.type,
                content.contentTitle,
                calendar.selectedDate
            ))
            .from(calendar)
            .join(calendar.content, content)
            .where(calendar.email.eq(email),
                calendar.type.eq(ActivityType.CONTENT),
                calendar.selectedDate.between(start, end))
            .fetch();
    }

    @Override
    public List<CalendarMonthlyResponse> findMonthlyGroupCalendars(String email, LocalDateTime start,
        LocalDateTime end) {
        return queryFactory
            .select(Projections.constructor(
                CalendarMonthlyResponse.class,
                calendar.id,
                calendar.type,
                group.title,
                group.groupDate
            ))
            .from(calendar)
            .join(calendar.group, group)
            .where(calendar.email.eq(email),
                calendar.type.eq(ActivityType.GROUP),
                group.groupDate.between(start, end))
            .fetch();
    }
}
