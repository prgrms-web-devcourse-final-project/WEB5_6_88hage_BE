package com.grepp.funfun.app.domain.contact.repository;

import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepositoryCustom {
    Page<Contact> findAllByEmailAndStatus(String email, ContactStatus status, Pageable pageable);

    Page<Contact> findAllForAdmin(ContactStatus status, Pageable pageable);
}
