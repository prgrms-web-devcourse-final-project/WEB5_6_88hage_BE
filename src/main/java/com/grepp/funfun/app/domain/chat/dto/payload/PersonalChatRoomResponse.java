package com.grepp.funfun.app.domain.chat.dto.payload;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import lombok.Builder;
import lombok.Data;

@Data
public class PersonalChatRoomResponse {

    private Long roomId;
    private ChatRoomType status;
    private String currentUserEmail;
    private String currentUserNickname;
    private String targetUserEmail;
    private String targetUserNickname;
    private Boolean targetUserDeleted;

    @Builder
    public PersonalChatRoomResponse(Long roomId, ChatRoomType status,
        String currentUserEmail,String currentUserNickname,String targetUserEmail, String targetUserNickname,
        Boolean targetUserDeleted) {
        this.roomId = roomId;
        this.status = status;
        this.currentUserEmail = currentUserEmail;
        this.currentUserNickname = currentUserNickname;
        this.targetUserEmail = targetUserEmail;
        this.targetUserNickname = targetUserNickname;
        this.targetUserDeleted = targetUserDeleted;
    }
}
