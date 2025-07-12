package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatRepository extends JpaRepository<Chat, Long>{

    List<Chat> findByRoomIdAndRoomTypeOrderByCreatedAt(Long roomId, ChatRoomType roomType);
}
