package com.grepp.funfun.app.domain.chat.service;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.dto.ChatDTO;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.chat.repository.ChatRepository;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.domain.chat.repository.PersonalChatRoomRepository;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
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
    private final GroupChatRoomRepository groupChatRoomRepository;
    private final PersonalChatRoomRepository personalChatRoomRepository;

    @Transactional
    public Chat saveChatMessage(ChatResponse chatResponse) {
        log.info("Saving message for RoomId: {}, RoomType: {}",
            chatResponse.getRoomId(), chatResponse.getRoomType());

        validateChatRoom(chatResponse.getRoomId(), chatResponse.getRoomType());

        Chat chat = Chat.builder()
            .roomType(chatResponse.getRoomType())
            .roomId(chatResponse.getRoomId())
            .senderNickname(chatResponse.getSenderNickname())
            .senderEmail(chatResponse.getSenderEmail())
            .message(chatResponse.getMessage())
            .build();

        Chat savedChat = chatRepository.save(chat);
        log.info("message save , chatId: {}", savedChat.getId());

        return savedChat;
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatHistory(Long roomId, ChatRoomType roomType) {
        log.debug("Getting chat history for roomId: {}", roomId);
        List<Chat> chatList = chatRepository.findByRoomIdAndRoomTypeOrderByCreatedAt(roomId, roomType);

        return chatList.stream()
            .map(ChatResponse::new)
            .collect(Collectors.toList());
    }

    private void validateChatRoom(Long roomId, ChatRoomType roomType) {
        switch (roomType) {
            case GROUP_CHAT:
                groupChatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("그룹 채팅방을 찾을 수 없습니다. roomId: " + roomId));
                break;
            case PERSONAL_CHAT:
                personalChatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new IllegalArgumentException("개인 채팅방을 찾을 수 없습니다. roomId: " + roomId));
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 채팅방 타입입니다: " + roomType);
        }
    }

    public void delete(final Long id) {
        chatRepository.deleteById(id);
    }

}
