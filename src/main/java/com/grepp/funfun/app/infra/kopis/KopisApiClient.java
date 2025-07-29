package com.grepp.funfun.app.infra.kopis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class KopisApiClient {

    @Value("${kopis.api.key}")
    private String kopisApiKey;

    private static final String BASE_URL = "http://www.kopis.or.kr/openApi/restful";

    public String fetchIdList(String dataType, String startDate, String endDate, String afterDate, int page) {
        if (!"pblprfr".equals(dataType)) {
            throw new IllegalArgumentException("지원되지 않는 데이터 타입: " + dataType);
        }
        String url = String.format("%s/%s?service=%s&stdate=%s&eddate=%s%s&cpage=%d&rows=100&signgucode=11",
                BASE_URL, dataType, kopisApiKey, startDate, endDate,
                (afterDate != null ? "&afterdate=" + afterDate : ""), page);
        return fetchXml(url);
    }

    public String fetchDetail(String contentId) {
        String url = String.format("%s/pblprfr/%s?service=%s", BASE_URL, contentId, kopisApiKey);
        return fetchXml(url);
    }

    private String fetchXml(String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/xml; charset=UTF-8");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
