package com.grepp.funfun.app.domain.chat.entity;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;

    @Enumerated(EnumType.STRING)
    private ChatRoomType status;

    private String name;

    @Builder
    public GroupChatRoom(Long groupId, ChatRoomType status, String name) {
        this.groupId = groupId;
        this.status = status;
        this.name = name;
    }

}
