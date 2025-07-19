package com.grepp.funfun.app.domain.contact.dto.payload;

import com.grepp.funfun.app.domain.contact.vo.ContactCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ContactRequest {
    @NotBlank(message = "문의 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "문의 내용은 필수입니다.")
    private String content;

    @NotNull(message = "문의 카테고리는 필수입니다.")
    private ContactCategory category;

    @Size(max = 5, message = "이미지는 최대 5개까지 업로드할 수 있습니다.")
    private List<MultipartFile> images;

    @NotNull(message = "이미지 변경 여부는 필수입니다.")
    private boolean imagesChanged;
}
