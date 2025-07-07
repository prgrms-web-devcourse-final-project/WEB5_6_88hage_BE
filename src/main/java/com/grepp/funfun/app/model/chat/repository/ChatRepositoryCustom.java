package com.grepp.funfun.app.model.chat.repository;

import com.grepp.funfun.app.model.chat.entity.Chat;
import java.util.List;

public interface ChatRepositoryCustom {
    List<Chat> findByGroupIdOrderByCreatedAt(Long groupId);
}
