package com.grepp.funfun.app.model.chat.repository;

import com.grepp.funfun.app.model.chat.entity.Chat;
import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRepository extends JpaRepository<Chat, Long>,ChatRepositoryCustom {

    Chat findFirstByRoom(ChatRoom chatRoom);

}
