package com.grepp.funfun.app.domain.group.dto.payload;

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
    private String userNickname;
    private List<String> participantNicknames ;
    private ParticipantStatus status;
    private List<String> participantEmails; // 참여자들 이메일
}


