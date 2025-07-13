package com.grepp.funfun.app.domain.chat.dto.payload;

import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {
    private Long chatId;
    private Long roomId;
    private ChatRoomType roomType;
    private String senderNickname;
    private String senderEmail;
    private String message;
    private String time;

    public ChatResponse(Chat chat) {
        this.chatId = chat.getId();
        this.roomId = chat.getRoomId();
        this.roomType = chat.getRoomType();
        this.senderNickname = chat.getSenderNickname();
        this.senderEmail = chat.getSenderEmail();
        this.message = chat.getMessage();
        this.time = chat.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}
