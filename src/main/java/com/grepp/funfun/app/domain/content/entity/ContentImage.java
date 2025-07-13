package com.grepp.funfun.app.domain.content.entity;

import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ContentImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    // DTO 변환 메서드 추가
    public ContentImageDTO toDTO() {
        return ContentImageDTO.builder()
                              .id(this.id)
                              .imageUrl(this.imageUrl)
                              .build();
    }
}
