package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.ChatRoom;
import com.grepp.funfun.app.domain.group.entity.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findFirstByGroup(Group group);
    Optional<ChatRoom> findByGroup_Id(Long groupId);
}
