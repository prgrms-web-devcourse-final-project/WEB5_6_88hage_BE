package com.grepp.funfun.app.model.chat.repository;

import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import com.grepp.funfun.app.model.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findFirstByGroup(Group group);

}
