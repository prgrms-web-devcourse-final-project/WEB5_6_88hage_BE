package com.grepp.funfun.app.domain.social.repository;

import com.grepp.funfun.app.domain.social.entity.Message;
import com.grepp.funfun.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findFirstBySender(User user);

    Message findFirstByReceiver(User user);

}
