package com.grepp.funfun.app.model.recommend.service;

import com.grepp.funfun.app.model.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.model.recommend.entity.ChatBot;
import com.grepp.funfun.app.model.recommend.repository.ChatBotRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ChatBotService {

    private final ChatBotRepository chatBotRepository;

    public ChatBotService(final ChatBotRepository chatBotRepository) {
        this.chatBotRepository = chatBotRepository;
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

    public Long create(final ChatBotDTO chatBotDTO) {
        final ChatBot chatBot = new ChatBot();
        mapToEntity(chatBotDTO, chatBot);
        return chatBotRepository.save(chatBot).getId();
    }

    public void update(final Long id, final ChatBotDTO chatBotDTO) {
        final ChatBot chatBot = chatBotRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(chatBotDTO, chatBot);
        chatBotRepository.save(chatBot);
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

    private ChatBot mapToEntity(final ChatBotDTO chatBotDTO, final ChatBot chatBot) {
        chatBot.setEmail(chatBotDTO.getEmail());
        chatBot.setGroupSummary(chatBotDTO.getGroupSummary());
        chatBot.setContentSummary(chatBotDTO.getContentSummary());
        chatBot.setType(chatBotDTO.getType());
        return chatBot;
    }

}
