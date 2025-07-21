package com.grepp.funfun.app.domain.chat.entity;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
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
@Builder
public class PersonalChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ChatRoomType status;

    private String name;

    private String userAEmail;
    private String userBEmail;

    @Builder.Default
    private Boolean userADeleted=false;
    @Builder.Default
    private Boolean userBDeleted=false;

    public void changeDeleted(String userEmail) {
        if (userEmail.equals(this.userAEmail)) {
            this.userADeleted = true;
        } else if (userEmail.equals(this.userBEmail)) {
            this.userBDeleted = true;
        } else {
            throw new CommonException(ResponseCode.BAD_REQUEST,"해당 유저는 채팅방 참여자가 아닙니다.");
        }
    }

}
