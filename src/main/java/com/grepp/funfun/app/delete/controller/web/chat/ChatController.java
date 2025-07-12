package com.grepp.funfun.app.delete.controller.web.chat;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.dto.ChatDTO;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.entity.GroupChatRoom;
import com.grepp.funfun.app.domain.chat.service.ChatService;
import com.grepp.funfun.app.domain.chat.repository.GroupChatRoomRepository;
import com.grepp.funfun.app.delete.util.CustomCollectors;
import com.grepp.funfun.app.delete.util.WebUtils;
import com.grepp.funfun.app.domain.chat.vo.ChatRoomType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final GroupChatRoomRepository groupChatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 그룹 채팅 메시지 전송
    @MessageMapping("/chat/message")
    public void sendMessage(ChatResponse chatResponse) {
        try {
            // roomType 설정 (프론트에서 보내지 않는 경우 대비)
            if (chatResponse.getRoomType() == null) {
                throw new IllegalArgumentException("roomType이 필요합니다.");
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

    @GetMapping("/personalChat")
    public String PersonalChat(Authentication authentication, Model model) {
        String userEmail = authentication.getName();
        model.addAttribute("userEmail", userEmail);
        return "chat/personalChat";
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("roomValues", groupChatRoomRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(GroupChatRoom::getId, GroupChatRoom::getId)));
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("chat") final ChatDTO chatDTO) {
        return "chat/add";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        chatService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("chat.delete.success"));
        return "redirect:/chats";
    }

}
