package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.group.entity.Group;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupChatRoomRepository extends JpaRepository<GroupChatRoom, Long> {

}
