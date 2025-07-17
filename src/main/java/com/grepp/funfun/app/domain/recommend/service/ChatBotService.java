package com.grepp.funfun.app.domain.recommend.service;

import com.grepp.funfun.app.domain.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.domain.recommend.entity.ChatBot;
import com.grepp.funfun.app.domain.recommend.repository.ChatBotRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final ChatBotRepository chatBotRepository;

    public Optional<ChatBot> findChatBotSummary(String email) {
        Optional<ChatBot> chatBot = chatBotRepository.findByEmailAndActivatedIsTrue(email);
        return chatBot;
    }

    @Transactional
    public void updateSummary(Long id, String summary) {
        chatBotRepository.updateSummary(id, summary);
    }

    @Transactional
    public void registSummary(ChatBot chatBot) {
        chatBotRepository.save(chatBot);
    }

    public List<ChatBotDTO> findAll() {
        final List<ChatBot> chatBots = chatBotRepository.findAll(Sort.by("id"));
        return chatBots.stream()
                .map(chatBot -> mapToDTO(chatBot, new ChatBotDTO()))
                .toList();
    }

    public ChatBotDTO get(final Long id) {
        return chatBotRepository.findById(id)
                .map(chatBot -> mapToDTO(chatBot, new ChatBotDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public void delete(final Long id) {
        chatBotRepository.deleteById(id);
    }

    private ChatBotDTO mapToDTO(final ChatBot chatBot, final ChatBotDTO chatBotDTO) {
        chatBotDTO.setId(chatBot.getId());
        chatBotDTO.setEmail(chatBot.getEmail());
        chatBotDTO.setGroupSummary(chatBot.getGroupSummary());
        chatBotDTO.setContentSummary(chatBot.getContentSummary());
        chatBotDTO.setType(chatBot.getType());
        return chatBotDTO;
    }



}
