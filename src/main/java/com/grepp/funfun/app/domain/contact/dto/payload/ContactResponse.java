package com.grepp.funfun.app.domain.contact.dto.payload;

import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.vo.ContactCategory;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContactResponse {
    private Long id;
    private ContactCategory category;
    private ContactStatus status;
    private String title;
    private LocalDateTime createdAt;
    private String writerEmail;

    public static ContactResponse from(Contact contact) {
        return ContactResponse.builder()
            .id(contact.getId())
            .category(contact.getCategory())
            .status(contact.getStatus())
            .title(contact.getTitle())
            .createdAt(contact.getCreatedAt())
            .writerEmail(contact.getUser().getEmail())
            .build();
    }
}
