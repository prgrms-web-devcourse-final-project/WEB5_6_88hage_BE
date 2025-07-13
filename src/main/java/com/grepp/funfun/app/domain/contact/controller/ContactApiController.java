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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping
    @Operation(summary = "문의 작성", description = "문의를 작성합니다.")
    public ResponseEntity<ApiResponse<String>> createContact(@RequestBody @Valid ContactRequest request, Authentication authentication) {
        String email = authentication.getName();
        contactService.create(email, request);
        return ResponseEntity.ok(ApiResponse.success("문의가 작성되었습니다."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateContact(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ContactDTO contactDTO) {
        contactService.update(id, contactDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable(name = "id") final Long id) {
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
