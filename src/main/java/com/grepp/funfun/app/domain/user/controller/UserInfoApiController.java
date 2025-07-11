package com.grepp.funfun.app.domain.user.controller;

import com.grepp.funfun.app.domain.user.dto.payload.ProfileRequest;
import com.grepp.funfun.app.domain.user.service.UserInfoService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/userInfos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserInfoApiController {

    private final UserInfoService userInfoService;

    @PutMapping
    @Operation(
        summary = "프로필 수정",
        description = """
            아래와 같은 형식으로 multipart/form-data 요청을 전송해 주세요.
            
            • image: 프로필 이미지 파일 (예: PNG, JPG 등 이미지 파일만 업로드 가능하게 해주세요.)
              - Swagger 에서는 이미지 업로드 테스트가 어려우므로 Postman 으로 테스트하는 것을 권장합니다.
            
            • imageChanged: 이미지 변경 여부 (true/false)
              - true: 이미지가 업로드되거나 삭제됩니다.
              - false: 서버는 이미지 변경을 무시합니다.
            
            • introduction: 사용자 한 줄 소개 텍스트
            
            • hashTags: 같은 키로 여러 번 전달해야 합니다.
              - 예시: hashTags=여행, hashTags=음악, hashTags=커피
            """
    )
    public ResponseEntity<ApiResponse<String>> updateProfile(
        @ModelAttribute @Valid ProfileRequest request, Authentication authentication) {
        String email = authentication.getName();
        userInfoService.update(email, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다."));
    }
}
