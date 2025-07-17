package com.grepp.funfun.app.domain.chat.entity;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class PersonalChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType status;

    private String name;

    private String userAEmail;
    private String userBEmail;

    @Builder
    public PersonalChatRoom(ChatRoomType status, String name, String userAEmail, String userBEmail) {
        this.status = status;
        this.name = name;
        this.userAEmail = userAEmail;
        this.userBEmail = userBEmail;
    }
}
