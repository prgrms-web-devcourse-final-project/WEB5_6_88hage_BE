package com.grepp.funfun.app.domain.chat.dto.payload;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import lombok.Builder;
import lombok.Data;

@Data
public class PersonalChatRoomResponse {

    private Long roomId;
    private String roomName;
    private ChatRoomType status;
    private String currentUserEmail;
    private String currentUserNickname;
    private String targetUserEmail;
    private String targetUserNickname;

    @Builder
    public PersonalChatRoomResponse(Long roomId, String roomName, ChatRoomType status,
        String currentUserEmail,String currentUserNickname,String targetUserEmail, String targetUserNickname) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.status = status;
        this.currentUserEmail = currentUserEmail;
        this.currentUserNickname = currentUserNickname;
        this.targetUserEmail = targetUserEmail;
        this.targetUserNickname = targetUserNickname;
    }
}
