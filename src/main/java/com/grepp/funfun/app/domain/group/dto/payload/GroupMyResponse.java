package com.grepp.funfun.app.domain.group.dto.payload;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMyResponse {

    private Long groupId;

    private String groupTitle;

    private String userEmail;// 참여자 이메일 (현재 사용자)

    private String groupImageUrl;

    private String userImageUrl;

    private String userNickname;

    private Integer participantCount;

    private ParticipantStatus status;

    private ChatRoomType type;
}


