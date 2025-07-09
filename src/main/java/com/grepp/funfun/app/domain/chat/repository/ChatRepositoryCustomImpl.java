package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.entity.QChat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QChat chat = QChat.chat;

    @Override
    public List<Chat> findByGroupOrderByCreatedAt(Long groupId) {
        return queryFactory
            .selectFrom(chat)
            .where(chat.room.group.id.eq(groupId))
            .orderBy(chat.createdAt.asc())
            .fetch();
    }
}
