package com.grepp.funfun.app.domain.user.controller;

import com.grepp.funfun.app.domain.user.dto.payload.ProfileRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserDetailResponse;
import com.grepp.funfun.app.domain.user.service.UserInfoService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/userInfos", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserInfoApiController {

    private final UserInfoService userInfoService;

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "프로필 수정",
        description = """
            아래와 같은 형식으로 multipart/form-data 요청을 전송해 주세요.
            
            • image: 프로필 이미지 파일 (예: PNG, JPG 등 이미지 파일만 업로드 가능하게 해주세요.)
              - 3MB 이하의 파일만 업로드 가능합니다.
            
            • imageChanged: 이미지 변경 여부 (true/false)
              - true: 이미지가 업로드되거나 삭제됩니다.
              - false: 서버는 이미지 변경을 무시합니다.
            
            • introduction: 사용자 한 줄 소개 텍스트
            """
    )
    public ResponseEntity<ApiResponse<String>> updateProfile(
        @ModelAttribute @Valid ProfileRequest request, Authentication authentication) {
        String email = authentication.getName();
        userInfoService.update(email, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다."));
    }

    @GetMapping("/{email}")
    @Operation(
        summary = "유저 상세 조회",
        description = """
            유저의 상세 정보를 이메일로 조회합니다.<br><br>
            응답 필드 설명:<br>
            - email: 이메일<br>
            - nickname: 닉네임<br>
            - introduction: 한 줄 소개<br>
            - imageUrl: 프로필 이미지 URL<br>
            - hashtags: 해시태그 리스트<br>
            - followerCount: 팔로워 수<br>
            - followingCount: 팔로잉 수<br>
            - groupLeadCount: 본인이 주최한 완료된 모임 수<br>
            - groupJoinCount: 본인이 참여한 완료된 모임 수 (리더 제외)
            """
    )
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetail(
        @PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.success(userInfoService.getUserDetail(email)));
    }
}
