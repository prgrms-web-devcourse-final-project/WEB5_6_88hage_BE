package com.grepp.funfun.app.model.chat.repository;

import com.grepp.funfun.app.model.chat.entity.Chat;
import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import com.grepp.funfun.app.model.group.entity.Group;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findFirstByGroup(Group group);
    Optional<ChatRoom> findByGroup_Id(Long groupId);
}
