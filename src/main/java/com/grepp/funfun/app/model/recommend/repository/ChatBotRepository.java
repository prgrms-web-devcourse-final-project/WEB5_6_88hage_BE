package com.grepp.funfun.app.model.recommend.repository;

import com.grepp.funfun.app.model.recommend.entity.ChatBot;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ChatBotRepository extends JpaRepository<ChatBot, Long> {
}
