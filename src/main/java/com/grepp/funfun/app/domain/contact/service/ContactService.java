package com.grepp.funfun.app.domain.contact.service;

import com.grepp.funfun.app.domain.contact.dto.ContactDTO;
import com.grepp.funfun.app.domain.contact.dto.payload.ContactRequest;
import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.repository.ContactRepository;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

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
            .status(ContactStatus.PENDING)
            .build();

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

        contact.setTitle(request.getTitle());
        contact.setContent(request.getContent());
    }

    public List<ContactDTO> findAll() {
        final List<Contact> contacts = contactRepository.findAll(Sort.by("id"));
        return contacts.stream()
                .map(contact -> mapToDTO(contact, new ContactDTO()))
                .toList();
    }

    public ContactDTO get(final Long id) {
        return contactRepository.findById(id)
                .map(contact -> mapToDTO(contact, new ContactDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ContactDTO contactDTO) {
        final Contact contact = new Contact();
        mapToEntity(contactDTO, contact);
        return contactRepository.save(contact).getId();
    }

    public void update(final Long id, final ContactDTO contactDTO) {
        final Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(contactDTO, contact);
        contactRepository.save(contact);
    }

    public void delete(final Long id) {
        contactRepository.deleteById(id);
    }

    private ContactDTO mapToDTO(final Contact contact, final ContactDTO contactDTO) {
        contactDTO.setId(contact.getId());
        contactDTO.setTitle(contact.getTitle());
        contactDTO.setContent(contact.getContent());
        contactDTO.setStatus(contact.getStatus());
        contactDTO.setAnswer(contact.getAnswer());
        contactDTO.setAnsweredAt(contact.getAnsweredAt());
        contactDTO.setUser(contact.getUser() == null ? null : contact.getUser().getEmail());
        return contactDTO;
    }

    private Contact mapToEntity(final ContactDTO contactDTO, final Contact contact) {
        contact.setTitle(contactDTO.getTitle());
        contact.setContent(contactDTO.getContent());
        contact.setStatus(contactDTO.getStatus());
        contact.setAnswer(contactDTO.getAnswer());
        contact.setAnsweredAt(contactDTO.getAnsweredAt());
        final User user = contactDTO.getUser() == null ? null : userRepository.findById(contactDTO.getUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        contact.setUser(user);
        return contact;
    }

}
