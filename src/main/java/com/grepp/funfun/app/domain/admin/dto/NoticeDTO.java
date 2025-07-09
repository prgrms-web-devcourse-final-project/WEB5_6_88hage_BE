package com.grepp.funfun.app.domain.admin.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NoticeDTO {

    private Long id;

    @Size(max = 255)
    private String message;

}
