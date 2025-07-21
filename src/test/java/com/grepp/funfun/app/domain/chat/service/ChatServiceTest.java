package com.grepp.funfun.app.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.repository.ChatRepository;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;
    // chat 저장하고 불러오기
    @Test
    public void saveChatHistory(){
        //Given
        Chat chat = Chat.builder()
            .senderEmail("aaa@aaa.aaa")
            .senderNickname("닉네임")
            .roomType(ChatRoomType.GROUP_CHAT)
            .roomId(1L)
            .message("제발 문제 없어라")
            .build();
        List<Chat> mockHistory = Collections.singletonList(chat);

        //WHEN
        when(chatRepository.save(chat)).thenReturn(chat);
        when(chatRepository.findByRoomIdAndRoomTypeOrderByCreatedAt(chat.getRoomId(), ChatRoomType.GROUP_CHAT))
            .thenReturn(mockHistory);

        Chat saveChat = chatRepository.save(chat);
        List<ChatResponse> history = chatService.getChatHistory(1L,ChatRoomType.GROUP_CHAT);

        //THEN
        assertThat(saveChat).isEqualTo(chat);
        assertThat(history).hasSize(1);
        assertThat(history.getFirst().getSenderEmail()).isEqualTo("aaa@aaa.aaa");
        assertThat(history.getFirst().getMessage()).isEqualTo("제발 문제 없어라");
    }
}
