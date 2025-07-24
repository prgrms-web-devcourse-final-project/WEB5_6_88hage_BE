package com.grepp.funfun.app.domain.admin.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.entity.QGroup;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class AdminGroupRepositoryImpl implements AdminGroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Group> findByStatus(GroupStatus status, Pageable pageable) {
        QGroup group = QGroup.group;

        // enum 이라서
        BooleanExpression statusCondition = (status != null) ? group.status.eq(status) : null;

        JPAQuery<Group> query = queryFactory.selectFrom(group);
        if (statusCondition != null) {
            query = query.where(statusCondition);
        }

        List<Group> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(group.count())
                .from(group)
                .where(statusCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
