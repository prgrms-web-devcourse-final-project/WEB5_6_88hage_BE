package com.grepp.funfun.app.domain.recommend.repository;

import com.grepp.funfun.app.domain.recommend.entity.QChatBot;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatBotRepositoryCustomImpl implements ChatBotRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QChatBot chatBot = QChatBot.chatBot;

    @Override
    public void updateSummary(Long id, String summary) {
        queryFactory
            .update(chatBot)
            .set(chatBot.contentSummary, summary)
            .where(chatBot.id.eq(id));
    }
}
