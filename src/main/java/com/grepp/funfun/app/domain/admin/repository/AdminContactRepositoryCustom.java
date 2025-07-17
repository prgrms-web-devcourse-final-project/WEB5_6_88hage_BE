package com.grepp.funfun.app.domain.admin.repository;

import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminContactRepositoryCustom {
    Page<Contact> findAllForAdmin(ContactStatus status, Pageable pageable);
}