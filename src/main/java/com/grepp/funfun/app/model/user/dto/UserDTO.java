package com.grepp.funfun.app.model.user.dto;

import com.grepp.funfun.app.model.auth.code.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.grepp.funfun.app.model.user.code.Gender;
import com.grepp.funfun.app.model.user.code.UserStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 255)
    private String password;

    @NotNull
    @Size(max = 255)
    private String nickname;

    private Integer age;

    private Gender gender;

    @Size(max = 255)
    private String tel;

    @Size(max = 255)
    private String address;

    private Role role;

    private UserStatus status;

    private LocalDate dueDate;

    private Integer suspendDuration;

    @Size(max = 255)
    private String dueReason;

    @JsonProperty("isVerified")
    private Boolean isVerified;

    @JsonProperty("isMarketingAgreed")
    private Boolean isMarketingAgreed;

    @Size(max = 255)
    private String info;

}
