package com.grepp.funfun.app.domain.group.dto.payload;

import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
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

    private String groupLeaderEmail;

    private String groupImageUrl;

    private String currentUserEmail;

    private String currentUserImageUrl;

    private String currentUserNickname;

    private Integer participantCount;

    private ParticipantStatus status;

    private ChatRoomType type;
}


