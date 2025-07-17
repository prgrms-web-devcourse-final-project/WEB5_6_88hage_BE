package com.grepp.funfun.app.domain.content.controller;

import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.content.service.KakaoGeoService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.grepp.funfun.app.infra.response.ResponseCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/content-data")
@RequiredArgsConstructor
@Slf4j
public class ContentDataPipeline {

    private final KakaoGeoService kakaoGeoService;
    private final ContentRepository contentRepository;

    @Operation(summary = "전체 콘텐츠 좌표 업데이트", description = "Kakao 주소 검색 API를 활용하여 DB의 모든 콘텐츠 좌표 정보를 갱신합니다.")
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateAllCoordinates() {
        Map<String, String> response = new HashMap<>();
        try {
            kakaoGeoService.updateAllContentCoordinates();
            response.put("message", "좌표 업데이트가 완료되었습니다.");
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("좌표 업데이트 중 오류 발생", e);
            response.put("message", "업데이트 실패: " + e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, response));
        }
    }

    // 1. 키워드 검색으로 위경도 가져오기 테스트
    @Operation(summary = "키워드 → 위경도 조회", description = "카카오 키워드 검색 API를 이용해 키워드로 위도/경도를 조회합니다.")
    @PostMapping("/keyword-coordinates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testKeywordToCoordinates(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("키워드 → 위경도 테스트 시작: {}", keyword);

            long startTime = System.currentTimeMillis();
            Optional<double[]> coordinates = kakaoGeoService.getCoordinatesFromKeywordSearch(keyword);
            long endTime = System.currentTimeMillis();

            result.put("success", coordinates.isPresent());
            result.put("input", keyword);
            result.put("responseTime", (endTime - startTime) + "ms");

            if (coordinates.isPresent()) {
                double[] coords = coordinates.get();
                result.put("latitude", coords[0]);
                result.put("longitude", coords[1]);
                log.info("키워드 → 위경도 성공: {} -> lat: {}, lng: {}", keyword, coords[0], coords[1]);
            } else {
                result.put("result", "검색 결과 없음");
                log.warn("키워드 → 위경도 실패: {}", keyword);
            }

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("키워드 → 위경도 테스트 오류: {}", keyword, e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, result));
        }
    }

    // 2. 위경도로 주소 가져오기 테스트 (역지오코딩)
    @Operation(summary = "위경도 → 주소 조회", description = "카카오 좌표→주소 API를 이용해 위도/경도로 주소를 조회합니다.")
    @PostMapping("/coordinates-address")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testCoordinesToAddress(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("위경도 → 주소 테스트 시작: lat: {}, lng: {}", latitude, longitude);

            long startTime = System.currentTimeMillis();
            Optional<String> address = kakaoGeoService.getAddressFromCoordinates(latitude, longitude);
            long endTime = System.currentTimeMillis();

            result.put("success", address.isPresent());
            result.put("inputLatitude", latitude);
            result.put("inputLongitude", longitude);
            result.put("responseTime", (endTime - startTime) + "ms");

            if (address.isPresent()) {
                result.put("address", address.get());
                log.info("위경도 → 주소 성공: lat: {}, lng: {} -> {}", latitude, longitude, address.get());
            } else {
                result.put("result", "주소 검색 결과 없음");
                log.warn("위경도 → 주소 실패: lat: {}, lng: {}", latitude, longitude);
            }

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("위경도 → 주소 테스트 오류: lat: {}, lng: {}", latitude, longitude, e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, result));
        }
    }

    // 3. 전체 프로세스 테스트 (키워드 → 위경도 → 주소)
    @Operation(summary = "전체 프로세스 테스트", description = "키워드 → 위경도 → 주소로 이어지는 전체 흐름을 테스트합니다.")
    @GetMapping("/full-process")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testFullProcess(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("전체 프로세스 테스트 시작: {}", keyword);

            long totalStartTime = System.currentTimeMillis();
            result.put("input", keyword);

            // 1단계: 키워드 → 위경도
            long step1Start = System.currentTimeMillis();
            Optional<double[]> coordinates = kakaoGeoService.getCoordinatesFromKeywordSearch(keyword);
            long step1End = System.currentTimeMillis();

            result.put("step1_success", coordinates.isPresent());
            result.put("step1_time", (step1End - step1Start) + "ms");

            if (coordinates.isEmpty()) {
                result.put("success", false);
                result.put("message", "1단계 실패: 키워드로 위경도를 찾을 수 없습니다.");
                return ResponseEntity.ok(ApiResponse.success(result));
            }

            double[] coords = coordinates.get();
            log.info("위경도 검색 결과: lat={}, lng={}", coords[0], coords[1]);
            result.put("latitude", coords[0]);
            result.put("longitude", coords[1]);

            // 2단계: 위경도 → 주소
            long step2Start = System.currentTimeMillis();
            Optional<String> address = kakaoGeoService.getAddressFromCoordinates(coords[0], coords[1]);
            long step2End = System.currentTimeMillis();

            result.put("step2_success", address.isPresent());
            result.put("step2_time", (step2End - step2Start) + "ms");

            if (address.isPresent()) {
                String finalAddress = address.get();
                result.put("finalAddress", address.get());
                result.put("rawAddress", finalAddress);
                result.put("success", true);
                result.put("message", "전체 프로세스 성공");
                log.info("전체 프로세스 성공: {} -> lat: {}, lng: {} -> {}",
                        keyword, coords[0], coords[1], finalAddress);
            } else {
                result.put("success", false);
                result.put("message", "2단계 실패: 위경도로 주소를 찾을 수 없습니다.");
            }

            result.put("totalTime", (System.currentTimeMillis() - totalStartTime) + "ms");
            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("전체 프로세스 테스트 오류: {}", keyword, e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ResponseCode.BAD_REQUEST, result));
        }
    }

}
