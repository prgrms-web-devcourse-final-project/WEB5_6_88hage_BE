package com.grepp.funfun.app.domain.recommend.repository;

import com.grepp.funfun.app.domain.recommend.entity.ChatBot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatBotRepository extends JpaRepository<ChatBot, Long>, ChatBotRepositoryCustom{

    Optional<ChatBot> findByEmailAndActivatedIsTrue(String email);

}
