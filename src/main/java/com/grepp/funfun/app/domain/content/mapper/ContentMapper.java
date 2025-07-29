package com.grepp.funfun.app.domain.content.mapper;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentImageDTO;
import com.grepp.funfun.app.domain.content.dto.ContentUrlDTO;
import com.grepp.funfun.app.domain.content.entity.*;
import com.grepp.funfun.app.domain.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.vo.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentMapper {

    private final ContentCategoryRepository contentCategoryRepository;

    public Content toEntity(ContentDTO dto) {
        if (dto == null) {
            log.warn("ContentDTO가 null입니다.");
            return null;
        }

        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            log.warn("카테고리가 없습니다: {}", dto.getContentTitle());
            return null;
        }

        ContentClassification classification;
        try {
            classification = ContentClassification.valueOf(dto.getCategory());
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 카테고리(enum 변환 실패): {} - 제목: {}", dto.getCategory(), dto.getContentTitle());
            return null;
        }

        ContentCategory category = contentCategoryRepository
                .findByCategory(classification)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다: " + dto.getCategory()));

        Content content = Content.builder()
                .externalId(dto.getExternalId())
                .contentTitle(dto.getContentTitle())
                .age(dto.getAge())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .fee(dto.getFee())
                .address(dto.getAddress())
                .area(dto.getArea())
                .guname(dto.getGuname())
                .time(dto.getTime())
                .runTime(dto.getRunTime())
                .startTime(dto.getStartTime())
                .poster(dto.getPoster())
                .description(dto.getDescription())
                .category(category)
                .bookmarkCount(dto.getBookmarkCount() != null ? dto.getBookmarkCount() : 0)
                .eventType(EventType.EVENT)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .images(new ArrayList<>())
                .urls(new ArrayList<>())
                .build();

        addImagesAndUrls(content, dto);
        return content;
    }

    public void updateEntity(Content content, ContentDTO dto) {
        content.setContentTitle(dto.getContentTitle());
        content.setAge(dto.getAge());
        content.setStartDate(dto.getStartDate());
        content.setEndDate(dto.getEndDate());
        content.setFee(dto.getFee());
        content.setArea(dto.getArea());
        content.setTime(dto.getTime());
        content.setRunTime(dto.getRunTime());
        content.setStartTime(dto.getStartTime());
        content.setPoster(dto.getPoster());
        content.setAddress(dto.getAddress());
        content.setGuname(dto.getGuname());
        content.setLatitude(dto.getLatitude());
        content.setLongitude(dto.getLongitude());

        content.getImages().clear();
        content.getUrls().clear();
        addImagesAndUrls(content, dto);
    }

    private void addImagesAndUrls(Content content, ContentDTO dto) {
        if (dto.getImages() != null) {
            for (ContentImageDTO imageDTO : dto.getImages()) {
                ContentImage image = ContentImage.builder()
                        .content(content)
                        .imageUrl(imageDTO.getImageUrl())
                        .build();
                content.getImages().add(image);
            }
        }

        if (dto.getUrls() != null) {
            for (ContentUrlDTO urlDTO : dto.getUrls()) {
                ContentUrl url = ContentUrl.builder()
                        .content(content)
                        .siteName(urlDTO.getSiteName())
                        .url(urlDTO.getUrl())
                        .build();
                content.getUrls().add(url);
            }
        }
    }
}

