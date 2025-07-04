package com.grepp.funfun.app.controller.api.faq.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaqCreateRequest {

    @NotBlank
    @Size(max = 255)
    private String question;

    @NotBlank
    @Size(max = 5000)
    private String answer;
}
