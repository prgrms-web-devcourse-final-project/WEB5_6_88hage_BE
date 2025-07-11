package com.grepp.funfun.app.domain.preference.controller;

import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceRequest;
import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceResponse;
import com.grepp.funfun.app.domain.preference.service.PreferenceService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/preferences", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PreferenceApiController {

    private final PreferenceService preferenceService;

    @PostMapping
    @Operation(summary = "사용자 취향 등록", description = "선택한 취향을 등록합니다.<br>첫 등록 시에만 사용 가능합니다.")
    public ResponseEntity<ApiResponse<String>> createContentPreference(
            @RequestBody @Valid PreferenceRequest request, Authentication authentication) {
        String email = authentication.getName();
        preferenceService.create(email, request);
        return ResponseEntity.ok(ApiResponse.success("취향이 등록되었습니다."));
    }

    @PutMapping
    @Operation(summary = "사용자 취향 수정", description = "선택한 취향으로 수정합니다.")
    public ResponseEntity<ApiResponse<String>> updateContentPreference(
        @RequestBody @Valid PreferenceRequest request, Authentication authentication) {
        String email = authentication.getName();
        preferenceService.update(email, request);
        return ResponseEntity.ok(ApiResponse.success("취향이 수정되었습니다."));
    }

    @GetMapping
    @Operation(summary = "사용자 취향 조회", description = "사용자 취향을 조회합니다.")
    public ResponseEntity<ApiResponse<PreferenceResponse>> getContentPreference(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(preferenceService.get(email)));
    }
}
