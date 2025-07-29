package com.grepp.funfun.app.infra.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoApiClient {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final AddressPreprocessor addressPreprocessor;

    public Optional<double[]> getCoordinatesFromKeywordSearch(String keyword) {
        try {
            log.info("원본 키워드: '{}'", keyword);
            String[] searchVariants = addressPreprocessor.optimizeKeywordForSearch(keyword);
            log.info("검색 시도할 키워드들: {}", java.util.Arrays.toString(searchVariants));

            for (String searchKeyword : searchVariants) {
                if (searchKeyword.trim().isEmpty()) continue;

                String encodedForLengthCheck = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
                if (encodedForLengthCheck.length() > 90) {
                    log.info("키워드가 길어서 스킵: {} ({}자)", searchKeyword, encodedForLengthCheck.length());
                    continue;
                }

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

    private Optional<double[]> performKeywordSearchForCoordinates(String keyword) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                    .queryParam("query", keyword)
                    .encode(StandardCharsets.UTF_8)
                    .build().toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(uri, HttpMethod.GET, entity, JsonNode.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode documents = response.getBody().get("documents");
                if (documents.isArray() && documents.size() > 0) {
                    JsonNode first = documents.get(0);
                    if (first.has("x") && first.has("y")) {
                        double lng = Double.parseDouble(first.get("x").asText());
                        double lat = Double.parseDouble(first.get("y").asText());
                        return Optional.of(new double[]{lat, lng});
                    }
                }
            }
        } catch (Exception e) {
            log.error("Kakao 키워드 검색 실패: {}", keyword, e);
        }
        return Optional.empty();
    }

    public Optional<String> getAddressFromCoordinates(double latitude, double longitude) {
        try {
            String url = String.format("https://dapi.kakao.com/v2/local/geo/coord2address.json?x=%f&y=%f", longitude, latitude);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode documents = response.getBody().get("documents");
                if (documents.isArray() && documents.size() > 0) {
                    JsonNode first = documents.get(0);
                    if (first.has("road_address") && !first.get("road_address").isNull()) {
                        return Optional.of(first.get("road_address").get("address_name").asText());
                    } else if (first.has("address") && !first.get("address").isNull()) {
                        return Optional.of(first.get("address").get("address_name").asText());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Kakao 역지오코딩 실패: {}, {}", latitude, longitude, e);
        }
        return Optional.empty();
    }
}
