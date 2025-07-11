package com.grepp.funfun.app.domain.participant.dto.payload;

import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.user.vo.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantResponse {

    private Long id;
    private String userEmail;
    private String userNickname;
    private Gender gender;
    private Long groupId;
    private ParticipantStatus status;
    private Boolean activated;

    // 정적 팩토리 메서드
    public static ParticipantResponse from(Participant participant) {
        return ParticipantResponse.builder()
            .id(participant.getId())
            .userEmail(participant.getUser().getEmail())
            .userNickname(participant.getUser().getNickname())
            .gender(participant.getUser().getGender())
            .groupId(participant.getGroup().getId())
            .status(participant.getStatus())
            .activated(participant.getUser().getActivated())
            .build();
    }
}
