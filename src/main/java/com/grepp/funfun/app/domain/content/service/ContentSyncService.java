package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.ContentGeoDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentCategoryRepository;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.infra.data.KopisContentDataLoader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentSyncService {

    private final ContentRepository contentRepository;
    private final KakaoGeoService kakaoGeoService; // 아까 만든 주소 처리 서비스
    private final KopisContentDataLoader kopisContentDataLoader; // API 호출부
    private final ContentCategoryRepository contentCategoryRepository;

//    @Transactional
//    public void fetchAndSaveContents() {
//        List<ContentDTO> externalContents = kopisContentDataLoader.fetchContents(); // 외부 API 요청
//
//        for (ContentDTO dto : externalContents) {
//            // 중복 방지 로직 필요 시 여기서
//            if (contentRepository.existsByContentTitle(dto.getContentTitle())) continue;
//
//            // 주소 전처리 및 위경도/자치구 추출
//            ContentGeoDTO geo = kakaoGeoService.process(dto.getAddress(), dto.getArea());
//
//            ContentCategory category = contentCategoryRepository
//                    .findByCategory(ContentClassification.valueOf(dto.getCategory()))
//                    .orElseThrow(() -> new IllegalArgumentException("잘못된 카테고리입니다: " + dto.getCategory()));
//
//            Content content = Content.builder()
//                    .contentTitle(dto.getContentTitle())
//                    .startDate(dto.getStartDate())
//                    .endDate(dto.getEndDate())
//                    .address(geo.getCombinedAddress())  // 정확한 주소 + 장소명
//                    .area(dto.getArea())
//                    .guname(geo.getGuname())
//                    .latitude(geo.getLatitude())
//                    .longitude(geo.getLongitude())
//                    .category(category)
//                    .description(dto.getDescription())
//                    .build();
//
//            contentRepository.save(content);
//        }
//    }
}
