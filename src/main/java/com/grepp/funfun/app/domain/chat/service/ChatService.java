package com.grepp.funfun.app.domain.chat.service;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.dto.ChatDTO;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.entity.ChatRoom;
import com.grepp.funfun.app.domain.chat.repository.ChatRepository;
import com.grepp.funfun.app.domain.chat.repository.ChatRoomRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public Chat saveChatMessage(ChatResponse chatResponse) {
        log.info("Saving message for teamId: {}", chatResponse.getGroupId());

        ChatRoom chatRoom = chatRoomRepository.findByGroup_Id(chatResponse.getGroupId())
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        Chat chat = Chat.builder()
            .room(chatRoom)
            .senderNickname(chatResponse.getSenderNickname())
            .senderEmail(chatResponse.getSenderEmail())
            .message(chatResponse.getMessage())
            .build();

        Chat savedChat = chatRepository.save(chat);
        log.info("메시지 저장 완료, chatId: {}", savedChat.getId());

        return savedChat;
    }
    @Transactional(readOnly = true)
    public List<ChatResponse> getChatHistory(Long groupId) {
        log.debug("Getting chat history for teamId: {}", groupId);
        List<Chat> chatList = chatRepository.findByGroupOrderByCreatedAt(groupId);

        return chatList.stream()
            .map(chat -> new ChatResponse(chat, groupId))
            .collect(Collectors.toList());
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
