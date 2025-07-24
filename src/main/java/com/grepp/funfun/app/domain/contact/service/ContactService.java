package com.grepp.funfun.app.domain.contact.service;

import com.grepp.funfun.app.domain.contact.dto.payload.ContactDetailResponse;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactRequest;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactResponse;
import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.entity.ContactImage;
import com.grepp.funfun.app.domain.contact.repository.ContactRepository;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.domain.s3.service.S3FileService;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final S3FileService s3FileService;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "존재하지 않는 사용자입니다: " + email));
    }

    private Contact getContact(Long contactId) {
        return contactRepository.findById(contactId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "존재하지 않는 문의입니다."));
    }

    @Transactional
    public void create(String email, ContactRequest request) {
        User user = getUser(email);

        Contact contact = Contact.builder()
            .user(user)
            .title(request.getTitle())
            .content(request.getContent())
            .category(request.getCategory())
            .status(ContactStatus.PENDING)
            .build();

        // 이미지 저장
        List<String> imageUrls = s3FileService.upload(request.getImages(), "contact");
        for (String url : imageUrls) {
            contact.getImages().add(ContactImage.builder()
                .imageUrl(url)
                .contact(contact)
                .build());
        }

        contactRepository.save(contact);
    }

    @Transactional
    public void update(Long contactId, String email, ContactRequest request) {
        Contact contact = getContact(contactId);

        if (contact.getStatus() == ContactStatus.COMPLETE) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "답변 완료된 문의는 수정할 수 없습니다.");
        }
        if (!contact.getUser().getEmail().equals(email)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "로그인한 사용자가 작성한 문의가 아닙니다.");
        }

        contact.updateForUser(request.getTitle(), request.getContent(), request.getCategory());

        // 이미지 변경 처리
        if (request.isImagesChanged()) {
            // 기존 이미지 삭제 (DB 에서만 삭제, S3 에는 그대로 있음)
            contact.getImages().clear();

            // 새 이미지 등록
            List<String> imageUrls = s3FileService.upload(request.getImages(), "contact");
            for (String url : imageUrls) {
                contact.getImages().add(ContactImage.builder()
                    .imageUrl(url)
                    .contact(contact)
                    .build());
            }
        }
    }

    @Transactional
    public void delete(Long contactId, String email) {
        Contact contact = getContact(contactId);

        if (!contact.getActivated()) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "이미 삭제한 문의입니다.");
        }
        if (!contact.getUser().getEmail().equals(email)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "로그인한 사용자가 작성한 문의가 아닙니다.");
        }

        contact.unActivated();
    }

    public ContactDetailResponse getDetail(Long contactId, String email) {
        Contact contact = getContact(contactId);

        if (!contact.getUser().getEmail().equals(email)) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "문의 작성자만 조회할 수 있습니다.");
        }

        return ContactDetailResponse.from(contact);
    }

    public Page<ContactResponse> findAll(String email, String status, Pageable pageable) {
        ContactStatus contactStatus = switch (status.toLowerCase()) {
            case "pending" -> ContactStatus.PENDING;
            case "complete" -> ContactStatus.COMPLETE;
            case "all" -> null;
            default -> throw new CommonException(ResponseCode.BAD_REQUEST, "잘못된 상태값입니다.");
        };

        return contactRepository.findAllByEmailAndStatus(email, contactStatus, pageable);
    }

}
