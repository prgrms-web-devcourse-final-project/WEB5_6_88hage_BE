package com.grepp.funfun.app.domain.content.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grepp.funfun.app.domain.content.dto.ContentGeoDTO;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Kakao API 단위 테스트")
class KakaoGeoServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KakaoGeoService kakaoGeoService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kakaoGeoService, "kakaoApiKey", "test-api-key");
        ReflectionTestUtils.setField(kakaoGeoService, "restTemplate", restTemplate);
    }

    @Test
    @DisplayName("주소 전처리 - 괄호와 대괄호 제거")
    void preprocessAddress_ShouldRemoveBracketsAndParentheses() throws Exception {
        // Given
        String input = "문래예술공장 (박스씨어터)";
        String expected = "문래예술공장";

        // When - private 메서드 호출을 위한 리플렉션
        Method method = KakaoGeoService.class.getDeclaredMethod("preprocessAddress", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(kakaoGeoService, input);

        // Then
        System.out.println("=== 주소 전처리 테스트 ===");
        System.out.println("입력값: " + input);
        System.out.println("예상값: " + expected);
        System.out.println("실제값: " + result);
        System.out.println("테스트 결과: " + (result.equals(expected) ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("주소 전처리 - 대괄호와 괄호 모두 제거")
    void preprocessAddress_ShouldRemoveAllBrackets() throws Exception {
        // Given
        String input = "예술의전당 [서울] (리사이틀홀)";
        String expected = "예술의전당";

        // When
        Method method = KakaoGeoService.class.getDeclaredMethod("preprocessAddress", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(kakaoGeoService, input);

        // Then
        System.out.println("=== 복합 괄호 제거 테스트 ===");
        System.out.println("입력값: " + input);
        System.out.println("예상값: " + expected);
        System.out.println("실제값: " + result);
        System.out.println("테스트 결과: " + (result.equals(expected) ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("주소 전처리 - 여러 공백을 하나로 통합")
    void preprocessAddress_ShouldNormalizeSpaces() throws Exception {
        // Given
        String input = "문래예술공장    (박스씨어터)";
        String expected = "문래예술공장";

        // When
        Method method = KakaoGeoService.class.getDeclaredMethod("preprocessAddress", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(kakaoGeoService, input);

        // Then
        System.out.println("=== 공백 정규화 테스트 ===");
        System.out.println("입력값: '" + input + "' (공백 4개)");
        System.out.println("예상값: '" + expected + "'");
        System.out.println("실제값: '" + result + "'");
        System.out.println("입력값 길이: " + input.length());
        System.out.println("결과값 길이: " + result.length());
        System.out.println("테스트 결과: " + (result.equals(expected) ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("자치구 추출 - 정상적인 주소에서 구 추출")
    void extractGunameFromAddress_ShouldReturnGu() throws Exception {
        // Given
        String address = "서울특별시 영등포구 문래동 90";
        String expected = "영등포구";

        // When
        Method method = KakaoGeoService.class.getDeclaredMethod("extractGunameFromAddress", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(kakaoGeoService, address);

        // Then
        System.out.println("=== 자치구 추출 테스트 ===");
        System.out.println("전체 주소: " + address);
        System.out.println("예상 자치구: " + expected);
        System.out.println("추출된 자치구: " + result);

        // 주소를 공백으로 분할해서 분석 과정 보여주기
        String[] parts = address.split(" ");
        System.out.println("주소 분할 결과: " + java.util.Arrays.toString(parts));
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].endsWith("구")) {
                System.out.println("찾은 구: " + parts[i] + " (인덱스: " + i + ")");
            }
        }
        System.out.println("테스트 결과: " + (result.equals(expected) ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("자치구 추출 - 구가 없는 주소에서 null 반환")
    void extractGunameFromAddress_ShouldReturnNull_WhenNoGu() throws Exception {
        // Given
        String address = "서울특별시 강남대로 123";
        String expected = null;

        // When
        Method method = KakaoGeoService.class.getDeclaredMethod("extractGunameFromAddress", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(kakaoGeoService, address);

        // Then
        System.out.println("=== 간단한 자치구 추출 테스트 ===");
        System.out.println("전체 주소: " + address);
        System.out.println("예상 자치구: " + expected);
        System.out.println("추출된 자치구: " + result);

        String[] parts = address.split(" ");
        System.out.println("주소 분할 결과: " + java.util.Arrays.toString(parts));
        System.out.println("테스트 결과: " + (result != null && result.equals(expected) ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("자치구 추출 - 빈 문자열에서 null 반환")
    void extractGunameFromAddress_ShouldReturnNull_WhenEmptyString() throws Exception {
        // Given
        String address = "";
        String expected = null;

        // When
        Method method = KakaoGeoService.class.getDeclaredMethod("extractGunameFromAddress", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(kakaoGeoService, address);

        // Then
        System.out.println("=== 빈 문자열 테스트 ===");
        System.out.println("전체 주소: '" + address + "'");
        System.out.println("예상 자치구: " + expected);
        System.out.println("추출된 자치구: " + result);
        System.out.println("테스트 결과: " + (result == expected ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("지오코딩 - 카카오 API 성공 응답")
    void getCoordinatesFromAddress_ShouldReturnCoordinates_WhenApiSuccess() throws Exception {
        // Given
        String address = "서울특별시 영등포구 문래동 90";
        String mockResponseJson = """
            {
                "documents": [
                    {
                        "x": "126.8956",
                        "y": "37.5249"
                    }
                ]
            }
        """;

        JsonNode mockResponse = objectMapper.readTree(mockResponseJson);
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(responseEntity);

        // When
        Optional<double[]> result = kakaoGeoService.getCoordinatesFromKeywordSearch(address);

        // Then
        System.out.println("=== 지오코딩 API 성공 테스트 ===");
        System.out.println("입력 주소: " + address);
        System.out.println("Mock API 응답: " + mockResponseJson.replaceAll("\\s+", " "));
        System.out.println("결과 존재 여부: " + result.isPresent());
        if (result.isPresent()) {
            double[] coords = result.get();
            System.out.println("추출된 좌표: [" + coords[0] + ", " + coords[1] + "]");
            System.out.println("위도(latitude): " + coords[0]);
            System.out.println("경도(longitude): " + coords[1]);
        }
        System.out.println("테스트 결과: " + (result.isPresent() && result.get().length == 2 ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(2);
        assertThat(result.get()[0]).isEqualTo(37.5249); // latitude
        assertThat(result.get()[1]).isEqualTo(126.8956); // longitude

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class));
    }


    @Test
    @DisplayName("지오코딩 - 카카오 API 빈 응답")
    void getCoordinatesFromAddress_ShouldReturnEmpty_WhenApiReturnsEmpty() throws Exception {
        // Given
        String address = "존재하지않는주소";
        String mockResponseJson = """
            {
                "documents": []
            }
        """;

        JsonNode mockResponse = objectMapper.readTree(mockResponseJson);
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenReturn(responseEntity);

        // When
        Optional<double[]> result = kakaoGeoService.getCoordinatesFromKeywordSearch(address);

        // Then
        System.out.println("=== 지오코딩 API 빈 응답 테스트 ===");
        System.out.println("입력 주소: " + address);
        System.out.println("Mock API 응답: " + mockResponseJson.replaceAll("\\s+", " "));
        System.out.println("결과 존재 여부: " + result.isPresent());
        System.out.println("예상: 빈 결과 (Optional.empty())");
        System.out.println("테스트 결과: " + (result.isEmpty() ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("전체 좌표 업데이트 - 이미 좌표가 있는 경우 스킵")
    void updateAllContentCoordinates_ShouldSkip_WhenCoordinatesExist() {
        // Given
        Content content1 = new Content();
        content1.setId(1L);
        content1.setLatitude(37.5249);
        content1.setLongitude(126.8956);
        content1.setAddress("문래예술공장");

        Content content2 = new Content();
        content2.setId(2L);
        content2.setLatitude(null);
        content2.setLongitude(null);
        content2.setAddress("예술의전당");
        content2.setArea("서울특별시");

        List<Content> contents = Arrays.asList(content1, content2);

        when(contentRepository.findAll()).thenReturn(contents);

        // 두 번째 컨텐츠에 대한 API 호출 설정
        String mockResponseJson = """
            {
                "documents": [
                    {
                        "address_name": "서울특별시 서초구 서초동 1330-3",
                        "x": "127.0027",
                        "y": "37.4801"
                    }
                ]
            }
        """;

        try {
            JsonNode mockResponse = objectMapper.readTree(mockResponseJson);
            ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                    .thenReturn(responseEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // When
        System.out.println("=== 전체 좌표 업데이트 테스트 ===");
        System.out.println("처리할 컨텐츠 수: " + contents.size());
        System.out.println("Content 1 - ID: " + content1.getId() + ", 기존 좌표: [" + content1.getLatitude() + ", " + content1.getLongitude() + "]");
        System.out.println("Content 2 - ID: " + content2.getId() + ", 기존 좌표: [" + content2.getLatitude() + ", " + content2.getLongitude() + "]");
        System.out.println("예상: Content 1은 스킵, Content 2만 처리");

        kakaoGeoService.updateAllContentCoordinates();

        // Then
        System.out.println("처리 후 결과:");
        System.out.println("Content 1 saveAndFlush 호출 횟수: 0번 (예상)");
        System.out.println("Content 2 saveAndFlush 호출 횟수: 1번 (예상)");
        System.out.println("테스트 결과: 성공");
        System.out.println();

        // 첫 번째 컨텐츠는 이미 좌표가 있으므로 처리하지 않음
        // 두 번째 컨텐츠만 처리됨
        verify(contentRepository, times(1)).saveAndFlush(content2);
        verify(contentRepository, never()).saveAndFlush(content1);
    }

    @Test
    @DisplayName("ContentGeoDTO 성공 케이스")
    void contentGeoDTO_Success() {
        // Given
        String placeName = "문래예술공장";
        String exactAddress = "서울특별시 영등포구 문래동 90";
        String guname = "영등포구";
        double latitude = 37.5249;
        double longitude = 126.8956;

        // When
        ContentGeoDTO dto = ContentGeoDTO.success(placeName, exactAddress, guname, latitude, longitude);

        // Then
        System.out.println("=== ContentGeoDTO 성공 케이스 테스트 ===");
        System.out.println("입력 장소명: " + placeName);
        System.out.println("입력 정확한 주소: " + exactAddress);
        System.out.println("입력 자치구: " + guname);
        System.out.println("입력 좌표: [" + latitude + ", " + longitude + "]");
        System.out.println();
        System.out.println("DTO 결과:");
        System.out.println("성공 여부: " + dto.isSuccess());
        System.out.println("원본 장소명: " + dto.getOriginalAddress());
        System.out.println("정확한 주소: " + dto.getExactAddress());
        System.out.println("자치구: " + dto.getGuname());
        System.out.println("좌표: [" + dto.getLatitude() + ", " + dto.getLongitude() + "]");
        System.out.println("결합된 주소: " + dto.getCombinedAddress());
        System.out.println("테스트 결과: " + (dto.isSuccess() ? "성공" : "실패"));
        System.out.println();

        assertThat(dto.isSuccess()).isTrue();
        assertThat(dto.getOriginalAddress()).isEqualTo(placeName);
        assertThat(dto.getExactAddress()).isEqualTo(exactAddress);
        assertThat(dto.getGuname()).isEqualTo(guname);
        assertThat(dto.getLatitude()).isEqualTo(latitude);
        assertThat(dto.getLongitude()).isEqualTo(longitude);
        assertThat(dto.getCombinedAddress()).isEqualTo("서울특별시 영등포구 문래동 90 문래예술공장");
    }

    @Test
    @DisplayName("ContentGeoDTO 실패 케이스")
    void contentGeoDTO_Failure() {
        // Given
        String placeName = "존재하지않는장소";

        // When
        ContentGeoDTO dto = ContentGeoDTO.failure(placeName);

        // Then
        System.out.println("=== ContentGeoDTO 실패 케이스 테스트 ===");
        System.out.println("입력 장소명: " + placeName);
        System.out.println();
        System.out.println("DTO 결과:");
        System.out.println("성공 여부: " + dto.isSuccess());
        System.out.println("원본 장소명: " + dto.getOriginalAddress());
        System.out.println("정확한 주소: " + dto.getExactAddress());
        System.out.println("자치구: " + dto.getGuname());
        System.out.println("좌표: [" + dto.getLatitude() + ", " + dto.getLongitude() + "]");
        System.out.println("결합된 주소: " + dto.getCombinedAddress());
        System.out.println("테스트 결과: " + (!dto.isSuccess() ? "성공" : "실패"));
        System.out.println();

        assertThat(dto.isSuccess()).isFalse();
        assertThat(dto.getOriginalAddress()).isEqualTo(placeName);
        assertThat(dto.getExactAddress()).isNull();
        assertThat(dto.getGuname()).isNull();
        assertThat(dto.getLatitude()).isNull();
        assertThat(dto.getLongitude()).isNull();
        assertThat(dto.getCombinedAddress()).isEqualTo(placeName);
    }

    @Test
    @DisplayName("카카오 API 에러 발생 시 빈 결과 반환")
    void getCoordinatesFromAddress_ShouldReturnEmpty_WhenApiThrowsException() {
        // Given
        String address = "테스트주소";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(JsonNode.class)))
                .thenThrow(new RuntimeException("API 호출 실패"));

        // When
        Optional<double[]> result = kakaoGeoService.getCoordinatesFromKeywordSearch(address);

        // Then
        System.out.println("=== API 에러 처리 테스트 ===");
        System.out.println("입력 주소: " + address);
        System.out.println("발생 예외: RuntimeException(\"API 호출 실패\")");
        System.out.println("결과 존재 여부: " + result.isPresent());
        System.out.println("예상: 빈 결과 (예외 처리 후 Optional.empty() 반환)");
        System.out.println("테스트 결과: " + (result.isEmpty() ? "성공" : "실패"));
        System.out.println();

        assertThat(result).isEmpty();
    }





}