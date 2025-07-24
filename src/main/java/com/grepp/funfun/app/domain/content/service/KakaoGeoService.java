package com.grepp.funfun.app.domain.content.service;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

            // 키워드 검색으로 위경도 가져오기
            Optional<double[]> coordinatesOpt = getCoordinatesFromKeywordSearch(searchAddress);
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

            // 위경도로 주소 가져오기 (역지오코딩)
            Optional<String> addressOpt = getAddressFromCoordinates(coordinates[0], coordinates[1]);
            if (addressOpt.isEmpty()) {
                contentRepository.delete(content);
                continue;
            }
            String fullAddress = addressOpt.get();

            if (area != null && !fullAddress.startsWith(area)) {
                log.warn("시/도 불일치: area={}, fullAddress={} → 삭제: {}", area, fullAddress, content.getId());
                contentRepository.delete(content);
                continue;
            }

            if (content.getGuname() == null) {
                String guname = extractGunameFromAddress(fullAddress);
                if (guname != null) {
                    content.setGuname(guname);
                }
            }

            String finalAddress = fullAddress + " " + cleanedPlaceName;
            content.setAddress(finalAddress.trim());

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
            String address = content.getAddress();
            String area = content.getArea() != null ? content.getArea() : "서울특별시";
            String cleanedPlaceName = preprocessAddress(address);
            String searchAddress = (cleanedPlaceName.contains(area)) ? cleanedPlaceName : area + " " + cleanedPlaceName;

            Optional<double[]> coordinatesOpt = getCoordinatesFromKeywordSearch(searchAddress);
            if (coordinatesOpt.isEmpty()) {
                log.warn("개별 위경도 검색 실패: {}", searchAddress);
                continue;
            }

            double[] coordinates = coordinatesOpt.get();
            content.setLatitude(coordinates[0]);
            content.setLongitude(coordinates[1]);

            Optional<String> addressOpt = getAddressFromCoordinates(coordinates[0], coordinates[1]);
            addressOpt.ifPresent(full -> {
                content.setAddress(full + " " + cleanedPlaceName);
                String guname = extractGunameFromAddress(full);
                if (guname != null) content.setGuname(guname);
            });

            contentRepository.save(content);
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
            log.info("원본 키워드: '{}'", keyword);
            String[] searchVariants = optimizeKeywordForSearch(keyword);
            log.info("검색 시도할 키워드들: {}", java.util.Arrays.toString(searchVariants));

            for (String searchKeyword : searchVariants) {
                if (searchKeyword.trim().isEmpty()) continue;

                // 길이 체크를 위한 임시 인코딩
                String encodedForLengthCheck = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
                if (encodedForLengthCheck.length() > 90) {
                    log.info("키워드가 길어서 스킵: {} (인코딩 길이: {}자)", searchKeyword, encodedForLengthCheck.length());
                    continue;
                }

                log.info("키워드 검색 시도: '{}'", searchKeyword);
                Optional<double[]> result = performKeywordSearchForCoordinates(searchKeyword);
                if (result.isPresent()) {
                    log.info("성공한 키워드: '{}'", searchKeyword);
                    return result;
                }
            }

        } catch (Exception e) {
            log.error("키워드 검색 에러: {}", keyword, e);
        }
        return Optional.empty();
    }

    private String[] optimizeKeywordForSearch(String keyword) {
        List<String> variants = new ArrayList<>();

        variants.add(keyword);

        String withoutSeoul = keyword.replaceAll("서울특별시\\s*", "")
                .replaceAll("서울시\\s*", "")
                .replaceAll("서울\\s*", "").trim();
        if (!withoutSeoul.isEmpty()) {
            variants.add(withoutSeoul);
        }

        String cleaned = keyword.replaceAll("\\([^)]*\\)", "")
                .replaceAll("\\[[^]]*\\]", "")
                .replaceAll("[()\\[\\]]", "")
                .trim();
        if (!cleaned.isEmpty()) {
            variants.add(cleaned);
        }

        String core = extractCoreKeyword(keyword);
        if (!core.isEmpty() && core.length() >= 2) {
            variants.add(core);
        }

        String[] words = withoutSeoul.split("\\s+");
        if (words.length > 0 && words[0].length() >= 2) {
            variants.add(words[0]);
        }

        return variants.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .distinct()
                .toArray(String[]::new);
    }

    private String extractCoreKeyword(String keyword) {
        String cleaned = keyword.replaceAll("서울특별시\\s*", "")
                .replaceAll("서울시\\s*", "")
                .replaceAll("서울\\s*", "")
                .replaceAll("\\([^)]*\\)", "")
                .trim();

        String[] words = cleaned.split("\\s+");
        if (words.length == 0) return "";

        String core = words[words.length - 1];

        if (core.matches(".*[가-힣].*") && core.length() >= 2) {
            return core;
        } else if (core.matches(".*[a-zA-Z].*") && core.length() >= 3) {
            return core;
        }
        return "";
    }

    private Optional<double[]> performKeywordSearchForCoordinates(String originalKeyword) {
        try {
            // 자동 인코딩 수행
            URI uri = UriComponentsBuilder
                    .fromHttpUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                    .queryParam("query", originalKeyword)
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            log.info("최종 호출 URL: {}", uri);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);

            log.info("키워드 검색 API 응답 상태: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode documents = response.getBody().get("documents");
                log.info("API 결과 documents: {}", documents.toPrettyString());

                if (documents.isArray() && documents.size() > 0) {
                    JsonNode first = documents.get(0);

                    if (first.has("x") && first.has("y")) {
                        double longitude = Double.parseDouble(first.get("x").asText());
                        double latitude = Double.parseDouble(first.get("y").asText());
                        log.info("first.get(\"x\") as text: {}, asDouble: {}", first.get("x").asText(), first.get("x").asDouble());


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
}