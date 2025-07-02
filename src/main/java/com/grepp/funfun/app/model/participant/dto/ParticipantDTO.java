package com.grepp.funfun.app.model.participant.dto;

import com.grepp.funfun.app.model.participant.code.ParticipantRole;
import com.grepp.funfun.app.model.participant.code.ParticipantStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ParticipantDTO {

    private Long id;

    private ParticipantRole role;

    private ParticipantStatus status;

    @NotNull
    @Size(max = 255)
    private String user;

    @NotNull
    private Long group;

}
