package com.grepp.funfun.app.domain.chat.controller;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.domain.chat.service.ChatService;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final GroupChatRoomRepository groupChatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 그룹 채팅 메시지 전송
    @MessageMapping("/message")
    public void sendMessage(ChatResponse chatResponse) {
        try {
            // roomType 설정 (프론트에서 보내지 않는 경우 대비)
            if (chatResponse.getRoomType() == null) {
                throw new IllegalArgumentException("roomType 이 필요합니다.");
            }

            Long chatRoomId = chatResponse.getRoomId();

            Chat savedChat = chatService.saveChatMessage(chatResponse);

            // 응답용 ChatResponse 생성
            ChatResponse responseToSend = new ChatResponse(savedChat);

            // roomType에 따라 적절한 채널 결정
            String channelType = chatResponse.getRoomType().equals(ChatRoomType.GROUP_CHAT) ? "group" : "personal";
            String destination = "/" + channelType + "/" + chatRoomId;

            log.info("========== {} 채팅 DB 저장완료 ===========", channelType);

            messagingTemplate.convertAndSend(destination, responseToSend);
            log.info("{} 메시지 전송 완료", destination);

        } catch (Exception e) {
            log.error("{} 메시지 처리 중 오류 발생: roomId={}, error={}",
                chatResponse.getRoomType(), chatResponse.getRoomId(), e.getMessage(), e);
        }
    }

    @GetMapping("/groupChat")
    public String groupChat(Authentication authentication, Model model) {
        String userEmail = authentication.getName();
        model.addAttribute("userEmail", userEmail);
        return "chat/groupChat";
    }

    @GetMapping("/autoSearch")
    public String autoSearch(Model model,String keyword) {
        model.addAttribute("keyword", keyword);
        return "chat/autoSearch";
    }

    @GetMapping("/personalChat")
    public String PersonalChat(Authentication authentication, Model model) {
        String userEmail = authentication.getName();
        model.addAttribute("userEmail", userEmail);
        return "chat/personalChat";
    }
}
