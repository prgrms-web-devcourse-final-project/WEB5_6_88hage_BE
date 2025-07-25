package com.grepp.funfun.app.domain.chat.entity;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderNickname;

    private String senderEmail;

    private String senderImageUrl;

    private String message;

    // 연관관계 제거하고 단순 참조로 변경
    @Enumerated(EnumType.STRING)
    private ChatRoomType roomType;

    private Long roomId;

    @Builder
    public Chat(ChatRoomType roomType, Long roomId, String senderNickname, String senderEmail,String senderImageUrl ,String message) {
        this.roomType = roomType;
        this.roomId = roomId;
        this.senderNickname = senderNickname;
        this.senderImageUrl = senderImageUrl;
        this.senderEmail = senderEmail;
        this.message = message;
    }
}
