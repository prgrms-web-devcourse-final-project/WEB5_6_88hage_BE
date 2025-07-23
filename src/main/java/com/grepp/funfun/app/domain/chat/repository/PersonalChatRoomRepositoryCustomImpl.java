package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.PersonalChatRoom;
import com.grepp.funfun.app.domain.chat.entity.QPersonalChatRoom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonalChatRoomRepositoryCustomImpl implements PersonalChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QPersonalChatRoom personalChatRoom = QPersonalChatRoom.personalChatRoom;

    @Override
    public List<PersonalChatRoom> findActiveRoomByUserEmail(String userEmail) {
        return queryFactory
            .selectFrom(personalChatRoom)
            .where(
                (personalChatRoom.userAEmail.eq(userEmail).and(personalChatRoom.userADeleted.eq(false)))
                    .or(personalChatRoom.userBEmail.eq(userEmail).and(personalChatRoom.userBDeleted.eq(false)))
            )
            .fetch();
    }
}
