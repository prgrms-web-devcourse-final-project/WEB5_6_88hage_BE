package com.grepp.funfun.app.domain.preference.controller;

import com.grepp.funfun.app.domain.preference.dto.payload.GroupPreferenceRequest;
import com.grepp.funfun.app.domain.preference.service.GroupPreferenceService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/groupPreferences", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GroupPreferenceApiController {

    private final GroupPreferenceService groupPreferenceService;

    @PostMapping
    @Operation(summary = "모임 취향 등록", description = "선택한 모임 취향을 등록합니다.<br>첫 등록 시에만 사용 가능합니다.")
    public ResponseEntity<ApiResponse<String>> createGroupPreference(
        @RequestBody @Valid GroupPreferenceRequest request, Authentication authentication) {
        String email = authentication.getName();
        groupPreferenceService.create(email, request);
        return ResponseEntity.ok(ApiResponse.success("모임 취향이 등록되었습니다."));
    }

}
