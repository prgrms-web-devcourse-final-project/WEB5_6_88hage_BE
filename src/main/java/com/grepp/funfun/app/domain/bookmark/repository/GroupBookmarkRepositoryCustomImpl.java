package com.grepp.funfun.app.domain.bookmark.repository;

import com.grepp.funfun.app.domain.bookmark.entity.GroupBookmark;
import com.grepp.funfun.app.domain.bookmark.entity.QGroupBookmark;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupBookmarkRepositoryCustomImpl implements GroupBookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QGroupBookmark qGroupBookmark = QGroupBookmark.groupBookmark;

    @Override
    public List<GroupBookmark> getMyGroupBookMarks(String userEmail) {
        return queryFactory
            .selectFrom(qGroupBookmark)
            .join(qGroupBookmark.group).fetchJoin()
            .where(qGroupBookmark.email.eq(userEmail))
            .fetch();
    }
}
