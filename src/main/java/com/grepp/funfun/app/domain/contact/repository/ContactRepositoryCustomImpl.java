package com.grepp.funfun.app.domain.contact.repository;

import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.entity.QContact;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ContactRepositoryCustomImpl implements ContactRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QContact contact = QContact.contact;
    private final QUser user = QUser.user;

    @Override
    public Page<Contact> findAllByEmailAndStatus(String email, ContactStatus status, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        // 활성화된 문의만 조회
        builder.and(contact.activated.isTrue());
        // 작성자 필터
        builder.and(contact.user.email.eq(email));

        // 상태 필터: 모두, 답변 진행, 답변 완료
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
