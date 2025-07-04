package com.grepp.funfun.app.model.chat.service;

import com.grepp.funfun.app.model.chat.dto.ChatDTO;
import com.grepp.funfun.app.model.chat.entity.Chat;
import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import com.grepp.funfun.app.model.chat.repository.ChatRepository;
import com.grepp.funfun.app.model.chat.repository.ChatRoomRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatService(final ChatRepository chatRepository,
            final ChatRoomRepository chatRoomRepository) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public List<ChatDTO> findAll() {
        final List<Chat> chats = chatRepository.findAll(Sort.by("id"));
        return chats.stream()
                .map(chat -> mapToDTO(chat, new ChatDTO()))
                .toList();
    }

    public ChatDTO get(final Long id) {
        return chatRepository.findById(id)
                .map(chat -> mapToDTO(chat, new ChatDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ChatDTO chatDTO) {
        final Chat chat = new Chat();
        mapToEntity(chatDTO, chat);
        return chatRepository.save(chat).getId();
    }

    public void update(final Long id, final ChatDTO chatDTO) {
        final Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(chatDTO, chat);
        chatRepository.save(chat);
    }

    public void delete(final Long id) {
        chatRepository.deleteById(id);
    }

    private ChatDTO mapToDTO(final Chat chat, final ChatDTO chatDTO) {
        chatDTO.setId(chat.getId());
        chatDTO.setSenderNickname(chat.getSenderNickname());
        chatDTO.setSenderEmail(chat.getSenderEmail());
        chatDTO.setMessage(chat.getMessage());
        chatDTO.setSendDate(chat.getCreatedAt());
        chatDTO.setRoom(chat.getRoom() == null ? null : chat.getRoom().getId());
        return chatDTO;
    }

    private Chat mapToEntity(final ChatDTO chatDTO, final Chat chat) {
        chat.setSenderNickname(chatDTO.getSenderNickname());
        chat.setSenderEmail(chatDTO.getSenderEmail());
        chat.setMessage(chatDTO.getMessage());
        final ChatRoom room = chatDTO.getRoom() == null ? null : chatRoomRepository.findById(chatDTO.getRoom())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        chat.setRoom(room);
        return chat;
    }

}
