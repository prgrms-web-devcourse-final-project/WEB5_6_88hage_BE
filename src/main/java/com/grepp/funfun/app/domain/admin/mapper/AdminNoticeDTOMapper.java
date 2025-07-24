package com.grepp.funfun.app.domain.admin.mapper;

import com.grepp.funfun.app.domain.admin.dto.AdminNoticeDTO;
import com.grepp.funfun.app.domain.admin.entity.AdminNotice;
import org.springframework.stereotype.Component;

@Component
public class AdminNoticeDTOMapper {

    public AdminNoticeDTO toDTO(AdminNotice entity) {
        return AdminNoticeDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public AdminNotice toEntity(AdminNoticeDTO dto) {
        return AdminNotice.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
    }
}
