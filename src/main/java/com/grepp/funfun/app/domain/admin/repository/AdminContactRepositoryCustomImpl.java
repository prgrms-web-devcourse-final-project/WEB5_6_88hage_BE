package com.grepp.funfun.app.domain.admin.repository;


import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.entity.QContact;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class AdminContactRepositoryCustomImpl implements AdminContactRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QContact contact = QContact.contact;
    private final QUser user = QUser.user;

    @Override
    public Page<Contact> findAllForAdmin(ContactStatus status, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(contact.activated.isTrue());

        if (status != null) {
            builder.and(contact.status.eq(status));
        }

        List<Contact> content = queryFactory
                .selectFrom(contact)
                .join(contact.user, user).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(contact.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(contact.count())
                .from(contact)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}