package com.grepp.funfun.app.domain.admin.service;

import com.grepp.funfun.app.domain.admin.dto.payload.AdminContactStatusResponse;
import com.grepp.funfun.app.domain.admin.dto.payload.AdminContactCategoryResponse;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactDetailResponse;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactResponse;
import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.repository.ContactRepository;
import com.grepp.funfun.app.domain.contact.vo.ContactCategory;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminContactQueryService {

    private final ContactRepository contactRepository;

    // 문의 전체 조회 (상태별로 필터링)
    @Transactional(readOnly = true)
    public Page<ContactResponse> findAll(String status, Pageable pageable) {
        ContactStatus contactStatus = switch (status.toLowerCase()) {
            case "pending" -> ContactStatus.PENDING;
            case "complete" -> ContactStatus.COMPLETE;
            case "all" -> null;
            default -> throw new CommonException(ResponseCode.BAD_REQUEST, "잘못된 상태값입니다.");
        };

        Page<Contact> contacts = contactRepository.findAllForAdmin(contactStatus, pageable);
        return contacts.map(ContactResponse::from);
    }

    // 문의 상태 목록
    public List<AdminContactStatusResponse> getAvailableStatuses() {
        return Arrays.stream(ContactStatus.values())
                .map(AdminContactStatusResponse::from)
                .toList();
    }

    // 문의 카테고리 목록
    public List<AdminContactCategoryResponse> getAvailableCategories() {
        return Arrays.stream(ContactCategory.values())
                .map(AdminContactCategoryResponse::from)
                .toList();
    }

    // 문의 상세 조회
    @Transactional(readOnly = true)
    public ContactDetailResponse getDetail(Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "존재하지 않는 문의입니다."));
        return ContactDetailResponse.from(contact);
    }
}
