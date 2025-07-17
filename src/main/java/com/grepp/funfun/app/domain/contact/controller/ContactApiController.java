package com.grepp.funfun.app.domain.contact.controller;

import com.grepp.funfun.app.domain.contact.dto.payload.ContactDetailResponse;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactRequest;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactResponse;
import com.grepp.funfun.app.domain.contact.service.ContactService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ContactApiController {

    private final ContactService contactService;

    @GetMapping
    @Operation(summary = "문의 목록 조회", description = """
        사용자의 문의 목록을 조회합니다.
        
        • status: 상태 필터를 지정할 수 있습니다.
        
            - all(모두), pending(답변 대기), complete(답변 완료)
        
        • page: 0 ~ N, 보고 싶은 페이지를 지정할 수 있습니다.
        
            - 기본값: 0
        
        • size: 기본 페이지당 항목 수
        
            - 기본값 : 10
        
        • sort: 정렬
        
            - 정렬 가능한 필드:
                        - `createdAt` (문의 작성한 시간)
        
            - 정렬 방식 예시:
                        - `?sort=createdAt,desc` (기본값, 최신순)
                        - `?sort=createdAt,asc` (오래된순)
        """)
    public ResponseEntity<ApiResponse<Page<ContactResponse>>> getAllContacts(
        Authentication authentication,
        @RequestParam(defaultValue = "all") String status,
        @Parameter(hidden = true)
        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
        @ParameterObject Pageable pageable) {
        String email = authentication.getName();
        Page<ContactResponse> contacts = contactService.findAll(email, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }

    @GetMapping("/{contactId}")
    @Operation(summary = "문의 상세 조회", description = "문의를 상세 조회합니다.")
    public ResponseEntity<ApiResponse<ContactDetailResponse>> getContact(
        @PathVariable Long contactId, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(contactService.getDetail(contactId, email)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "문의 작성",
        description = """
            아래와 같은 형식으로 multipart/form-data 요청을 전송해 주세요.
            
            • title: 문의 제목
            
            • content: 문의 내용
            
            • category: 문의 카테고리 (필수, ENUM 값 중 하나 선택)
              - 예: GENERAL, REPORT
            
            • images: 문의 관련 이미지 파일들 (선택)
              - 최대 5개까지 업로드 가능
              - 각 파일은 3MB 이하의 파일만 업로드 가능합니다.
              - 같은 키(images)로 여러 개 업로드해야 합니다.
            
            • imagesChanged: 이미지 변경 여부 (true/false)
              - true: 이미지가 업로드되거나 삭제됩니다.
              - false: 서버는 이미지 변경을 무시합니다.
            """
    )
    public ResponseEntity<ApiResponse<String>> createContact(
        @ModelAttribute @Valid ContactRequest request, Authentication authentication) {
        String email = authentication.getName();
        contactService.create(email, request);
        return ResponseEntity.ok(ApiResponse.success("문의가 작성되었습니다."));
    }

    @PutMapping(path = "/{contactId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "문의 수정",
        description = """
            답변 완료 상태가 아닌 문의를 수정합니다.
            
            아래와 같은 형식으로 multipart/form-data 요청을 전송해 주세요.
            
            • title: 문의 제목
            
            • content: 문의 내용
            
            • category: 문의 카테고리 (필수, ENUM 값 중 하나 선택)
              - 예: GENERAL, REPORT
            
            • images: 문의 관련 이미지 파일들 (선택)
              - 최대 5개까지 업로드 가능
              - 각 파일은 3MB 이하의 파일만 업로드 가능합니다.
              - 같은 키(images)로 여러 개 업로드해야 합니다.
            
            • imagesChanged: 이미지 변경 여부 (true/false)
              - true: 이미지가 업로드되거나 삭제됩니다.
              - false: 서버는 이미지 변경을 무시합니다.
            """
    )
    public ResponseEntity<ApiResponse<String>> updateContact(@PathVariable Long contactId,
        @ModelAttribute @Valid ContactRequest request, Authentication authentication) {
        String email = authentication.getName();
        contactService.update(contactId, email, request);
        return ResponseEntity.ok(ApiResponse.success("문의가 수정되었습니다."));
    }

    @PatchMapping("/{contactId}")
    @Operation(summary = "문의 삭제", description = "문의를 삭제(비활성화)합니다.")
    public ResponseEntity<Void> deleteContact(@PathVariable Long contactId,
        Authentication authentication) {
        String email = authentication.getName();
        contactService.delete(contactId, email);
        return ResponseEntity.noContent().build();
    }

}
