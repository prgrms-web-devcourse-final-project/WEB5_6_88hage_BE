package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.mapper.ContentMapper;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentPersistenceService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;

    @Transactional
    public Optional<Content> saveOrUpdate(ContentDTO dto) {
        try {
            Content existing = contentRepository.findByExternalId(dto.getExternalId()).orElse(null);

            if (existing != null) {
                contentMapper.updateEntity(existing, dto);
                contentRepository.save(existing);
                log.debug("기존 콘텐츠 업데이트 완료: {}", existing.getContentTitle());
                return Optional.of(existing);
            }

            Content content = contentMapper.toEntity(dto);
            if (content == null) {
                log.debug("Content 변환 실패: {}", dto.getExternalId());
                return Optional.empty();
            }

            contentRepository.save(content);
            log.debug("신규 콘텐츠 저장 완료: {}", content.getContentTitle());
            return Optional.of(content);

        } catch (Exception e) {
            log.debug("콘텐츠 저장 실패: {} - {}", dto.getExternalId(), e.getMessage());
            return Optional.empty();
        }
    }
}
