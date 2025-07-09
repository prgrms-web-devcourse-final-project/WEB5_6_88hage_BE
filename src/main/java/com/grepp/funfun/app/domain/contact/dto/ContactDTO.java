package com.grepp.funfun.app.domain.contact.dto;

import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContactDTO {

    private Long id;

    @Size(max = 255)
    private String title;

    @Size(max = 255)
    private String content;

    private ContactStatus status;

    @Size(max = 255)
    private String answer;

    private LocalDateTime answeredAt;

    @NotNull
    @Size(max = 255)
    private String user;

}
