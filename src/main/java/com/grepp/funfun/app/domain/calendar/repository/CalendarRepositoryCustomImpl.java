package com.grepp.funfun.app.domain.calendar.repository;

import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarDailyResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarMonthlyResponse;
import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import com.grepp.funfun.app.domain.calendar.entity.QCalendar;
import com.grepp.funfun.app.domain.content.entity.QContent;
import com.grepp.funfun.app.domain.group.entity.QGroup;
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
                content.id,
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
                group.id,
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

    @Override
    public List<CalendarDailyResponse> findDailyContentCalendars(String email, LocalDateTime start,
        LocalDateTime end) {
        return queryFactory
            .select(Projections.constructor(
                CalendarDailyResponse.class,
                calendar.id,
                calendar.type,
                content.id,
                content.contentTitle,
                calendar.selectedDate,
                content.address
            ))
            .from(calendar)
            .join(calendar.content, content)
            .where(calendar.email.eq(email),
                calendar.type.eq(ActivityType.CONTENT),
                calendar.selectedDate.between(start, end))
            .fetch();
    }

    @Override
    public List<CalendarDailyResponse> findDailyGroupCalendars(String email, LocalDateTime start,
        LocalDateTime end) {
        return queryFactory
            .select(Projections.constructor(
                CalendarDailyResponse.class,
                calendar.id,
                calendar.type,
                group.id,
                group.title,
                group.groupDate,
                group.address
            ))
            .from(calendar)
            .join(calendar.group, group)
            .where(calendar.email.eq(email),
                calendar.type.eq(ActivityType.GROUP),
                group.groupDate.between(start, end))
            .fetch();
    }
}
