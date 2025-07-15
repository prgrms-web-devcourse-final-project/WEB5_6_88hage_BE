package com.grepp.funfun.app.domain.user.dto.payload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileRequest {

    private MultipartFile image;

    @NotNull(message = "이미지 변경 여부는 필수입니다.")
    private boolean imageChanged;

    private String introduction;
}