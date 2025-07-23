package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.PersonalChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalChatRoomRepository extends JpaRepository<PersonalChatRoom, Long> , PersonalChatRoomRepositoryCustom{
    // 내가 참여한 모든 개인 채팅방 조회 (OR 조건)
    List<PersonalChatRoom> findByUserAEmailOrUserBEmail(String userAEmail, String userBEmail);

    // 특정 두 사용자 간의 채팅방 찾기 (AND 조건)
    Optional<PersonalChatRoom> findByUserAEmailAndUserBEmail(String userAEmail, String userBEmail);
};

