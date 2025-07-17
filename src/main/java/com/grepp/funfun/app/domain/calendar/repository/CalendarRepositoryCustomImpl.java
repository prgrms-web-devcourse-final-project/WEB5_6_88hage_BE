package com.grepp.funfun.app.domain.calendar.repository;

import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarContentResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarDailyResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarMonthlyResponse;
import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import com.grepp.funfun.app.domain.calendar.entity.QCalendar;
import com.grepp.funfun.app.domain.content.entity.QContent;
import com.grepp.funfun.app.domain.group.entity.QGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

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

    @Override
    public Page<CalendarContentResponse> findContentByEmail(String email, boolean pastIncluded, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(calendar.email.eq(email));
        builder.and(calendar.type.eq(ActivityType.CONTENT));
        if (!pastIncluded) {
            LocalDateTime today = LocalDate.now().atStartOfDay();
            builder.and(calendar.selectedDate.goe(today));
        }

        List<CalendarContentResponse> result = queryFactory
            .select(Projections.constructor(
                CalendarContentResponse.class,
                calendar.id,
                content.id,
                content.contentTitle,
                content.category.category,
                calendar.selectedDate
            ))
            .from(calendar)
            .join(calendar.content, content)
            .where(builder)
            .orderBy(getOrderSpecifiers(pageable.getSort()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(calendar.count())
            .from(calendar)
            .where(builder);

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        return sort.stream()
            .map(order -> {
                String property = order.getProperty();
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;

                return switch (property) {
                    case "selectedDate" -> new OrderSpecifier<>(direction, calendar.selectedDate);
                    default -> new OrderSpecifier<>(Order.DESC, calendar.selectedDate);
                };
            }).toArray(OrderSpecifier[]::new);
    }
}
