package com.grepp.funfun.app.domain.contact.controller;

import com.grepp.funfun.app.domain.contact.dto.ContactDTO;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactRequest;
import com.grepp.funfun.app.domain.contact.service.ContactService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ContactApiController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<List<ContactDTO>> getAllContacts() {
        return ResponseEntity.ok(contactService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getContact(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(contactService.get(id));
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
    public ResponseEntity<ApiResponse<String>> createContact(@ModelAttribute @Valid ContactRequest request, Authentication authentication) {
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
    public ResponseEntity<ApiResponse<String>> updateContact(@PathVariable Long contactId, @ModelAttribute @Valid ContactRequest request, Authentication authentication) {
        String email = authentication.getName();
        contactService.update(contactId, email, request);
        return ResponseEntity.ok(ApiResponse.success("문의가 수정되었습니다."));
    }

    @PatchMapping("/{contactId}")
    @Operation(summary = "문의 삭제", description = "문의를 삭제(비활성화)합니다.")
    public ResponseEntity<Void> deleteContact(@PathVariable Long contactId, Authentication authentication) {
        String email = authentication.getName();
        contactService.delete(contactId, email);
        return ResponseEntity.noContent().build();
    }

}
