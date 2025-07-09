package com.grepp.funfun.app.domain.contact.service;

import com.grepp.funfun.app.domain.contact.dto.ContactDTO;
import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.repository.ContactRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(final ContactRepository contactRepository,
            final UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
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
