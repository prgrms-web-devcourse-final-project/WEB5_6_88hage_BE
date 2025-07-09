package com.grepp.funfun.app.delete.controller.web.chat;

import com.grepp.funfun.app.domain.chat.dto.payload.ChatResponse;
import com.grepp.funfun.app.domain.chat.dto.ChatDTO;
import com.grepp.funfun.app.domain.chat.entity.Chat;
import com.grepp.funfun.app.domain.chat.service.ChatService;
import com.grepp.funfun.app.domain.chat.entity.ChatRoom;
import com.grepp.funfun.app.domain.chat.repository.ChatRoomRepository;
import com.grepp.funfun.app.delete.util.CustomCollectors;
import com.grepp.funfun.app.delete.util.WebUtils;
import jakarta.validation.Valid;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/message")
    public void sendMessage(ChatResponse chatResponse) {
        try{
            Long groupId = chatResponse.getGroupId();

            Chat savedChat = chatService.saveChatMessage(chatResponse);
            chatResponse.setChatId(savedChat.getId());
            chatResponse.setTime(savedChat.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")));
            log.info("==========DB 저장완료===========");

            messagingTemplate.convertAndSend("/room/" + groupId, chatResponse);
            log.info("/room/{} 메시지 전송 완료", groupId);

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: groupId={}, error={}",
                chatResponse.getGroupId(), e.getMessage(), e);
        }
    }
    @GetMapping("/test")
    public String test(){
        return "chat/chatTest";
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("roomValues", chatRoomRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ChatRoom::getId, ChatRoom::getId)));
    }


    @GetMapping
    public String list(final Model model) {
        model.addAttribute("chats", chatService.findAll());
        return "chat/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("chat") final ChatDTO chatDTO) {
        return "chat/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("chat") @Valid final ChatDTO chatDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chat/add";
        }
        chatService.create(chatDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chat.create.success"));
        return "redirect:/chats";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("chat", chatService.get(id));
        return "chat/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("chat") @Valid final ChatDTO chatDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chat/edit";
        }
        chatService.update(id, chatDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chat.update.success"));
        return "redirect:/chats";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        chatService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("chat.delete.success"));
        return "redirect:/chats";
    }

}
