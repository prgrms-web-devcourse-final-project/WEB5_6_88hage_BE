package com.grepp.funfun.app.domain.chat.repository;

import com.grepp.funfun.app.domain.chat.entity.Chat;
import java.util.List;

public interface ChatRepositoryCustom {
    List<Chat> findByGroupOrderByCreatedAt(Long groupId);
}
