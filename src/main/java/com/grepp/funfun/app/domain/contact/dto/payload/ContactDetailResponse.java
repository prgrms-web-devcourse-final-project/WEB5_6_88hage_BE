package com.grepp.funfun.app.domain.contact.dto.payload;

import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.entity.ContactImage;
import com.grepp.funfun.app.domain.contact.vo.ContactCategory;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactDetailResponse {
    private Long id;
    private String title;
    private String content;
    private ContactCategory category;
    private ContactStatus status;
    private String answer;
    private LocalDateTime answeredAt;
    private LocalDateTime createdAt;
    private List<String> imageUrls;

    public static ContactDetailResponse from(Contact contact) {
        return ContactDetailResponse.builder()
            .id(contact.getId())
            .title(contact.getTitle())
            .content(contact.getContent())
            .category(contact.getCategory())
            .status(contact.getStatus())
            .answer(contact.getAnswer())
            .answeredAt(contact.getAnsweredAt())
            .createdAt(contact.getCreatedAt())
            .imageUrls(contact.getImages().stream().map(ContactImage::getImageUrl).toList())
            .build();
    }
}
