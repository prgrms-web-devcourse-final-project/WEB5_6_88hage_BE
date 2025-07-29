package com.grepp.funfun.app.infra.kakao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class KakaoApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AddressPreprocessor addressPreprocessor;

    @InjectMocks
    private KakaoApiClient kakaoApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("키워드로 위경도 가져오기 성공")
    void testGetCoordinatesFromKeywordSearch_Success() throws Exception {
        String keyword = "서울시 강남구 역삼동";
        String[] variants = new String[]{"강남구 역삼동"};

        JsonNode mockNode = new ObjectMapper().readTree("""
            {
              "documents": [
                {
                  "x": "127.037",
                  "y": "37.501"
                }
              ]
            }
        """);

        when(addressPreprocessor.optimizeKeywordForSearch(anyString())).thenReturn(variants);
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(mockNode));

        Optional<double[]> result = kakaoApiClient.getCoordinatesFromKeywordSearch(keyword);

        assertThat(result).isPresent();
        assertThat(result.get()[0]).isEqualTo(37.501);
        assertThat(result.get()[1]).isEqualTo(127.037);
    }

    @Test
    @DisplayName("역지오코딩 성공")
    void testGetAddressFromCoordinates_Success() throws Exception {
        JsonNode mockNode = new ObjectMapper().readTree("""
            {
              "documents": [
                {
                  "road_address": {
                    "address_name": "서울특별시 강남구 테헤란로 123"
                  }
                }
              ]
            }
        """);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(ResponseEntity.ok(mockNode));

        Optional<String> result = kakaoApiClient.getAddressFromCoordinates(37.501, 127.037);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("서울특별시 강남구 테헤란로 123");
    }

    @Test
    @DisplayName("키워드 검색 실패시 Optional.empty 반환")
    void testGetCoordinatesFromKeywordSearch_Fail() throws Exception {
        when(addressPreprocessor.optimizeKeywordForSearch(anyString())).thenReturn(new String[]{""});

        Optional<double[]> result = kakaoApiClient.getCoordinatesFromKeywordSearch("잘못된키워드");

        assertThat(result).isEmpty();
    }
}
