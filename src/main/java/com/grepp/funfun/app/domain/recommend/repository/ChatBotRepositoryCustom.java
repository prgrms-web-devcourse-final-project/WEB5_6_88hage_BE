package com.grepp.funfun.app.domain.recommend.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface ChatBotRepositoryCustom {

    void updateSummary(Long id, String summary);
}
