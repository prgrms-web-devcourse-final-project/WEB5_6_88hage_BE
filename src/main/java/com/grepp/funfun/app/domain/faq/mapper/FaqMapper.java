package com.grepp.funfun.app.domain.faq.mapper;

import com.grepp.funfun.app.domain.faq.dto.FaqDTO;
import com.grepp.funfun.app.domain.faq.entity.Faq;
import org.springframework.stereotype.Component;

@Component
public class FaqMapper {

    public FaqDTO toDto(Faq faq) {
        FaqDTO dto = new FaqDTO();
        dto.setId(faq.getId());
        dto.setTitle(faq.getTitle());
        dto.setContent(faq.getContent());
        dto.setCreatedAt(faq.getCreatedAt());
        return dto;
    }

    public Faq toEntity(FaqDTO dto) {
        Faq faq = new Faq();
        faq.setTitle(dto.getTitle());
        faq.setContent(dto.getContent());
        return faq;
    }

    public void updateEntity(FaqDTO dto, Faq faq) {
        faq.setTitle(dto.getTitle());
        faq.setContent(dto.getContent());
    }
}

