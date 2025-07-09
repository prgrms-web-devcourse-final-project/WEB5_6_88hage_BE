package com.grepp.funfun.app.domain.contact.repository;

import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContactRepository extends JpaRepository<Contact, Long> {

    Contact findFirstByUser(User user);

}
