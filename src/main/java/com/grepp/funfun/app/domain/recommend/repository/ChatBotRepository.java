package com.grepp.funfun.app.domain.recommend.repository;

import com.grepp.funfun.app.domain.recommend.entity.ChatBot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface ChatBotRepository extends JpaRepository<ChatBot, Long> {

    Optional<ChatBot> findByEmailAndActivatedIsTrue(String email);

    @Modifying
    @Query("update ChatBot c set c.contentSummary = :summary where c.id = :id")
    void updateSummary(Long id, String summary);
}
