package com.grepp.funfun.app.domain.contact.service;

import com.grepp.funfun.app.domain.contact.dto.payload.ContactDetailResponse;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactRequest;
import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.entity.ContactImage;
import com.grepp.funfun.app.domain.contact.repository.ContactRepository;
import com.grepp.funfun.app.domain.contact.vo.ContactCategory;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.domain.s3.service.S3FileService;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @InjectMocks
    private ContactService contactService;

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3FileService s3FileService;

    private String email;

    @BeforeEach
    void setUp() {
        email = "test@test.test";
    }


    @Test
    void create_OK() {
        // given
        ContactRequest request = ContactRequest.builder().build();

        // when
        when(userRepository.findById(email)).thenReturn(Optional.of(new User()));
        when(s3FileService.upload((List<MultipartFile>) null, "contact")).thenReturn(List.of("url1"));
        contactService.create(email, request);

        // then
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void update_NOT_IMAGE_CHANGED_OK() {
        // given
        ContactRequest request = ContactRequest.builder()
            .title("문의 수정 제목")
            .content("문의 수정 내용")
            .category(ContactCategory.GENERAL)
            .imagesChanged(false)
            .build();

        Contact contact = Contact.builder()
            .status(ContactStatus.PENDING)
            .user(User.builder().email(email).build())
            .build();

        // when
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        contactService.update(1L, email, request);

        // then
        assertEquals("문의 수정 제목", contact.getTitle());
        assertEquals("문의 수정 내용", contact.getContent());
        assertEquals(ContactCategory.GENERAL, contact.getCategory());
    }

    @Test
    void update_IMAGE_CHANGED_OK() {
        // given
        ContactRequest request = ContactRequest.builder()
            .title("문의 수정 제목")
            .content("문의 수정 내용")
            .category(ContactCategory.GENERAL)
            .imagesChanged(true)
            .build();

        Contact contact = Contact.builder()
            .status(ContactStatus.PENDING)
            .user(User.builder().email(email).build())
            .images(new ArrayList<>(List.of(
                ContactImage.builder()
                .imageUrl("url1")
                .build())
            ))
            .build();

        // when
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        when(s3FileService.upload((List<MultipartFile>) null, "contact")).thenReturn(List.of("url2"));
        contactService.update(1L, email, request);

        // then
        assertEquals("문의 수정 제목", contact.getTitle());
        assertEquals("문의 수정 내용", contact.getContent());
        assertEquals(ContactCategory.GENERAL, contact.getCategory());
        assertEquals("url2", contact.getImages().getFirst().getImageUrl());
    }

    @Test
    void update_ALREADY_COMPLETE_EX() {
        // given
        ContactRequest request = ContactRequest.builder()
            .build();

        Contact contact = Contact.builder()
            .status(ContactStatus.COMPLETE)
            .build();

        // when, then
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        assertThrows(CommonException.class, () -> contactService.update(1L, email, request));
    }

    @Test
    void update_UNAUTHORIZED_EX() {
        // given
        ContactRequest request = ContactRequest.builder()
            .build();

        Contact contact = Contact.builder()
            .status(ContactStatus.PENDING)
            .user(User.builder().email("othet@other.other").build())
            .build();

        // when, then
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        assertThrows(CommonException.class, () -> contactService.update(1L, email, request));
    }

    @Test
    void delete_OK() {
        // given
        Contact contact = Contact.builder()
            .user(User.builder().email(email).build())
            .build();

        // when
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        contactService.delete(1L, email);

        // then
        assertEquals(false, contact.getActivated());
    }

    @Test
    void delete_ALREADY_DELETED_EX() {
        // given
        Contact contact = Contact.builder()
            .user(User.builder().email(email).build())
            .build();
        contact.unActivated();

        // when, then
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        assertThrows(CommonException.class, () -> contactService.delete(1L, email));
    }

    @Test
    void delete_UNAUTHORIZED_EX() {
        // given
        Contact contact = Contact.builder()
            .user(User.builder().email("othet@other.other").build())
            .build();

        // when, then
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        assertThrows(CommonException.class, () -> contactService.delete(1L, email));
    }

    @Test
    void getDetail_OK() {
        // given
        Contact contact = Contact.builder()
            .id(1L)
            .title("문의 상세 조회")
            .user(User.builder().email(email).build())
            .build();

        // when
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        ContactDetailResponse detail = contactService.getDetail(1L, email);

        // then
        assertEquals(1L, detail.getId());
        assertEquals("문의 상세 조회", detail.getTitle());
    }

    @Test
    void getDetail_UNAUTHORIZED_EX() {
        // given
        Contact contact = Contact.builder()
            .user(User.builder().email("othet@other.other").build())
            .build();

        // when, then
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));
        assertThrows(CommonException.class, () -> contactService.getDetail(1L, email));
    }

    @Test
    void findAll_INVALID_STATUS_EX() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when & then
        assertThrows(CommonException.class, () -> contactService.findAll(email, "invalid", pageable));
    }
}