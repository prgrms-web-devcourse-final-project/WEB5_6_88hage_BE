package com.grepp.funfun.app.controller.web.chat;

import com.grepp.funfun.app.model.chat.dto.ChatDTO;
import com.grepp.funfun.app.model.chat.service.ChatService;
import com.grepp.funfun.app.model.chat.entity.ChatRoom;
import com.grepp.funfun.app.model.chat.repository.ChatRoomRepository;
import com.grepp.funfun.util.CustomCollectors;
import com.grepp.funfun.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/chats")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;

    public ChatController(final ChatService chatService,
            final ChatRoomRepository chatRoomRepository) {
        this.chatService = chatService;
        this.chatRoomRepository = chatRoomRepository;
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
