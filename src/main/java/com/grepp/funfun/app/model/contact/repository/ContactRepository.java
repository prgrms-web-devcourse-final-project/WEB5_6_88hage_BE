package com.grepp.funfun.app.model.contact.repository;

import com.grepp.funfun.app.model.contact.entity.Contact;
import com.grepp.funfun.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContactRepository extends JpaRepository<Contact, Long> {

    Contact findFirstByUser(User user);

}
