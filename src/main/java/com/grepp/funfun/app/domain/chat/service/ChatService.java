package com.grepp.funfun.app.domain.chat.service;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.repository.ChatRepository;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.domain.chat.repository.PersonalChatRoomRepository;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        // todo 나중에 지울 코드
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

        return chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatHistory(Long roomId, ChatRoomType roomType) {
        // todo : 나중에 지울 코드
        log.debug("Getting chat history for roomId: {}", roomId);
        List<Chat> chatList = chatRepository.findByRoomIdAndRoomTypeOrderByCreatedAt(roomId, roomType);

        return chatList.stream()
            .map(ChatResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ChatResponse> getLastChatHistory(Long roomId, ChatRoomType roomType) {
        // todo : 나중에 지울 코드
        log.debug("Getting chat history for roomId: {}", roomId);
        return chatRepository.findTop1ByRoomIdAndRoomTypeOrderByCreatedAtDesc(roomId, roomType)
            .map(ChatResponse::new);
    }

    private void validateChatRoom(Long roomId, ChatRoomType roomType) {
        switch (roomType) {
            case GROUP_CHAT:
                groupChatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND,"그룹 채팅방을 찾을 수 없습니다. roomId: " + roomId));
                break;
            case PERSONAL_CHAT:
                personalChatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND,"개인 채팅방을 찾을 수 없습니다. roomId: " + roomId));
                break;
            default:
                throw new CommonException(ResponseCode.NOT_FOUND,"알 수 없는 채팅방 타입입니다: " + roomType);
        }
    }

    public void delete(final Long id) {
        chatRepository.deleteById(id);
    }

}
