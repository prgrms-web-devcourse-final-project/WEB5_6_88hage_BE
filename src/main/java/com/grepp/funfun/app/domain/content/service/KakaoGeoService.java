package com.grepp.funfun.app.domain.content.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.grepp.funfun.app.domain.content.dto.ContentGeoDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoGeoService {

    private final ContentRepository contentRepository;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public void updateAllContentCoordinates() {
        List<Content> contents = contentRepository.findAll();

        for (Content content : contents) {
            if (!isTargetCategory(content.getCategory())) {
                continue;
            }

            String originalPlaceName = content.getAddress();
            String cleanedPlaceName = preprocessAddress(originalPlaceName);

            String area = content.getArea() != null ? content.getArea() : "서울특별시";
            String searchAddress = (cleanedPlaceName.contains(area)) ? cleanedPlaceName : area + " " + cleanedPlaceName;

            // 키워드 검색으로 바로 위경도 가져오기
            Optional<double[]> coordinatesOpt = getCoordinatesFromKeywordSearch(searchAddress);
            if (coordinatesOpt.isEmpty()) {
                log.warn("위경도 검색 실패: {} → {}", content.getId(), searchAddress);
                continue;
            }
            double[] coordinates = coordinatesOpt.get();

            // 위경도 저장
            if (content.getLatitude() == null || content.getLongitude() == null) {
                content.setLatitude(coordinates[0]);
                content.setLongitude(coordinates[1]);
            }

            // 위경도로 주소 가져오기 (역지오코딩)
            Optional<String> addressOpt = getAddressFromCoordinates(coordinates[0], coordinates[1]);
            if (addressOpt.isPresent()) {
                String fullAddress = addressOpt.get();

                // 구 이름 추출
                if (content.getGuname() == null) {
                    String guname = extractGunameFromAddress(fullAddress);
                    if (guname != null) {
                        content.setGuname(guname);
                    }
                }

                // 최종 주소 설정
                String finalAddress = fullAddress + " " + cleanedPlaceName;
                content.setAddress(finalAddress.trim());
            } else {
                log.warn("역지오코딩 실패: {} → lat: {}, lng: {}", content.getId(), coordinates[0], coordinates[1]);
            }

            contentRepository.saveAndFlush(content);
            log.info("저장 완료: {} → {}", content.getId(), content.getAddress());

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean isTargetCategory(ContentCategory category) {
        if (category == null || category.getCategory() == null) return false;

        return switch (category.getCategory()) {
            case THEATER, DANCE, POP_DANCE, CLASSIC, GUKAK,
                 POP_MUSIC, MIX, MAGIC, MUSICAL -> true;
            default -> false;
        };
    }

    private String preprocessAddress(String rawAddress) {
        String processed = rawAddress
                .replaceAll("\\([^)]*\\)", "")
                .replaceAll("\\[[^]]*\\]", "")
                .replaceAll("[()\\[\\]]", "")
                .trim();

        return processed.replaceAll("\\s+", " ");
    }

    private String extractGunameFromAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return null;
        }
        String[] parts = fullAddress.split(" ");
        for (String part : parts) {
            if (part.endsWith("구")) {
                return part;
            }
        }
        return null;
    }

    // 키워드 검색으로 바로 위경도 가져오기
    public Optional<double[]> getCoordinatesFromKeywordSearch(String keyword) {
        try {
            String[] searchVariants = optimizeKeywordForSearch(keyword);

            for (String searchKeyword : searchVariants) {
                if (searchKeyword.trim().isEmpty()) continue;

                String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
                if (encodedKeyword.length() > 90) {
                    log.info("키워드가 길어서 스킵: {} ({}자)", searchKeyword, encodedKeyword.length());
                    continue;
                }

                log.info("키워드 검색 시도: '{}' (인코딩 후 {}자)", searchKeyword, encodedKeyword.length());
                Optional<double[]> result = performKeywordSearchForCoordinates(encodedKeyword, searchKeyword);
                if (result.isPresent()) {
                    return result;
                }
            }

        } catch (Exception e) {
            log.error("키워드 검색 에러: {}", keyword, e);
        }
        return Optional.empty();
    }

    private String[] optimizeKeywordForSearch(String keyword) {
        return new String[] {
                keyword.replaceAll("서울특별시\\s*", "").trim(),
                keyword.replaceAll("서울특별시\\s*", "")
                        .replaceAll("서울시\\s*", "")
                        .replaceAll("서울\\s*", "").trim(),
                extractCoreKeyword(keyword),
                keyword.length() > 20 ? keyword.substring(0, 20) : keyword
        };
    }

    private String extractCoreKeyword(String keyword) {
        String cleaned = keyword.replaceAll("서울특별시\\s*", "")
                .replaceAll("서울시\\s*", "")
                .replaceAll("서울\\s*", "")
                .trim();

        String[] words = cleaned.split("\\s+");
        if (words.length == 0) return cleaned;
        String core = words[words.length - 1];
        return core.length() < 3 ? "" : core;
    }

    private Optional<double[]> performKeywordSearchForCoordinates(String encodedKeyword, String originalKeyword) {
        try {
            String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + encodedKeyword;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

            log.info("키워드 검색 API 응답 상태: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode documents = response.getBody().get("documents");

                if (documents.isArray() && documents.size() > 0) {
                    JsonNode first = documents.get(0);

                    if (first.has("x") && first.has("y")) {
                        double longitude = first.get("x").asDouble();
                        double latitude = first.get("y").asDouble();

                        log.info("키워드 검색 성공: {} -> lat: {}, lng: {}", originalKeyword, latitude, longitude);
                        return Optional.of(new double[]{latitude, longitude});
                    }
                }
            }
        } catch (Exception e) {
            log.error("키워드 검색 API 호출 에러: {}", originalKeyword, e);
        }
        return Optional.empty();
    }

    // 위경도로 주소 가져오기 (역지오코딩)
    public Optional<String> getAddressFromCoordinates(double latitude, double longitude) {
        try {
            String url = String.format("https://dapi.kakao.com/v2/local/geo/coord2address.json?x=%f&y=%f",
                    longitude, latitude);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode documents = response.getBody().get("documents");

                if (documents.isArray() && documents.size() > 0) {
                    JsonNode first = documents.get(0);

                    // 도로명 주소 우선, 없으면 지번 주소 사용
                    if (first.has("road_address") && !first.get("road_address").isNull()) {
                        String roadAddress = first.get("road_address").get("address_name").asText();
                        log.info("역지오코딩 성공(도로명): lat: {}, lng: {} -> {}", latitude, longitude, roadAddress);
                        return Optional.of(roadAddress);
                    } else if (first.has("address") && !first.get("address").isNull()) {
                        String address = first.get("address").get("address_name").asText();
                        log.info("역지오코딩 성공(지번): lat: {}, lng: {} -> {}", latitude, longitude, address);
                        return Optional.of(address);
                    }
                }
            }
        } catch (Exception e) {
            log.error("역지오코딩 에러: lat: {}, lng: {}", latitude, longitude, e);
        }
        return Optional.empty();
    }

    // 기존 지오코딩 메소드 (필요시 사용)
//    public Optional<double[]> getCoordinatesFromGeocoding(String address) {
//        try {
//            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
//            String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + encodedAddress;
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
//
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                JsonNode documents = response.getBody().get("documents");
//
//                if (documents.isArray() && documents.size() > 0) {
//                    JsonNode first = documents.get(0);
//                    double longitude = first.get("x").asDouble();
//                    double latitude = first.get("y").asDouble();
//                    log.info("지오코딩 성공: {} -> lat: {}, lng: {}", address, latitude, longitude);
//                    return Optional.of(new double[]{latitude, longitude});
//                }
//            }
//        } catch (Exception e) {
//            log.error("지오코딩 에러: {}", address, e);
//        }
//        return Optional.empty();
//    }
}