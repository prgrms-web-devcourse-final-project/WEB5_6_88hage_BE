package com.grepp.funfun.app.domain.chat.dto.payload;

import com.grepp.funfun.app.domain.chat.entity.Chat;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatResponse {
    private Long chatId;
    private Long groupId;
    private String senderNickname;
    private String senderEmail;
    private String message;
    private String time;

    public ChatResponse(Chat chat, Long groupId) {
        this.chatId = chat.getId();
        this.groupId = groupId;
        this.senderNickname = chat.getSenderNickname();
        this.senderEmail = chat.getSenderEmail();
        this.message = chat.getMessage();
        this.time = chat.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

}
