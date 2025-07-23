package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.PersonalChatRoom;
import java.util.List;

public interface PersonalChatRoomRepositoryCustom {

    List<PersonalChatRoom> findActiveRoomByUserEmail(String userEmail);

}
