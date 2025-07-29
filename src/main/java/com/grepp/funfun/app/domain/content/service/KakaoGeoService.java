package com.grepp.funfun.app.domain.content.service;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.infra.kakao.AddressPreprocessor;
import com.grepp.funfun.app.infra.kakao.KakaoApiClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoGeoService {

    private final ContentRepository contentRepository;
    private final KakaoApiClient kakaoApiClient;
    private final AddressPreprocessor addressPreprocessor;

    @Transactional
    public void updateAllContentCoordinates() {
        List<Content> contents = contentRepository.findAll();

        for (Content content : contents) {
            if (!addressPreprocessor.isTargetCategory(content.getCategory())) continue;

            String cleanedPlaceName = addressPreprocessor.preprocessAddress(content.getAddress());
            if (cleanedPlaceName == null || cleanedPlaceName.isBlank()) {
                log.warn("주소 전처리 실패: {} - {}", content.getId(), content.getAddress());
                continue;
            }
            String area = content.getArea() != null ? content.getArea() : "서울특별시";
            String searchAddress = cleanedPlaceName.contains(area) ? cleanedPlaceName : area + " " + cleanedPlaceName;

            Optional<double[]> coordinatesOpt = kakaoApiClient.getCoordinatesFromKeywordSearch(searchAddress);
            if (coordinatesOpt.isEmpty()) {
                log.warn("위경도 검색 실패: {} → {}", content.getId(), searchAddress);
                contentRepository.delete(content);
                continue;
            }
            double[] coordinates = coordinatesOpt.get();
            if (content.getLatitude() == null || content.getLongitude() == null) {
                content.setLatitude(coordinates[0]);
                content.setLongitude(coordinates[1]);
            }

            Optional<String> addressOpt = kakaoApiClient.getAddressFromCoordinates(coordinates[0], coordinates[1]);
            if (addressOpt.isEmpty()) {
                contentRepository.delete(content);
                continue;
            }
            String fullAddress = addressOpt.get();
            if (!fullAddress.startsWith(area)) {
                log.warn("시/도 불일치: area={}, fullAddress={} → 삭제: {}", area, fullAddress, content.getId());
                contentRepository.delete(content);
                continue;
            }

            if (content.getGuname() == null) {
                content.setGuname(addressPreprocessor.extractGunameFromAddress(fullAddress));
            }

            content.setAddress((fullAddress + " " + cleanedPlaceName).trim());
            contentRepository.saveAndFlush(content);
            log.info("저장 완료: {} → {}", content.getId(), content.getAddress());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Transactional
    public void updateContentCoordinates(List<Content> contents) {
        for (Content content : contents) {
            String cleanedPlaceName = addressPreprocessor.preprocessAddress(content.getAddress());
            String area = content.getArea() != null ? content.getArea() : "서울특별시";
            String searchAddress = cleanedPlaceName.contains(area) ? cleanedPlaceName : area + " " + cleanedPlaceName;

            Optional<double[]> coordinatesOpt = kakaoApiClient.getCoordinatesFromKeywordSearch(searchAddress);
            if (coordinatesOpt.isEmpty()) {
                log.warn("개별 위경도 검색 실패: {}", searchAddress);
                contentRepository.delete(content);
                continue;
            }
            double[] coordinates = coordinatesOpt.get();
            content.setLatitude(coordinates[0]);
            content.setLongitude(coordinates[1]);

            Optional<String> addressOpt = kakaoApiClient.getAddressFromCoordinates(coordinates[0], coordinates[1]);
            if (addressOpt.isEmpty()) {
                contentRepository.delete(content);
                continue;
            }
            String fullAddress = addressOpt.get();
            if (!fullAddress.startsWith(area)) {
                log.warn("개별 업데이트 - 시/도 불일치: area={}, fullAddress={} → 삭제: {}", area, fullAddress, content.getId());
                contentRepository.delete(content);
                continue;
            }

            content.setAddress(fullAddress + " " + cleanedPlaceName);
            content.setGuname(addressPreprocessor.extractGunameFromAddress(fullAddress));
            contentRepository.save(content);
        }
    }
}
