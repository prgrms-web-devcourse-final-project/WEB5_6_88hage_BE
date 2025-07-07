package com.grepp.funfun.app.controller.api.participant.payload;

import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import com.grepp.funfun.app.model.participant.entity.Participant;
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
    private Long groupId;
    private ParticipantStatus status;
    private Boolean activated;

    // 정적 팩토리 메서드
    public static ParticipantResponse from(Participant participant) {
        return ParticipantResponse.builder()
            .id(participant.getId())
            .userEmail(participant.getUser().getEmail())
            .groupId(participant.getGroup().getId())
            .status(participant.getStatus())
            .activated(participant.getUser().getActivated())
            .build();
    }
}
