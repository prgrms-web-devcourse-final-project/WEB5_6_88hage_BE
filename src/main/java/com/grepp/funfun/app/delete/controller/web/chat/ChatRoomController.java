package com.grepp.funfun.app.delete.controller.web.chat;

import com.grepp.funfun.app.domain.chat.dto.ChatRoomDTO;
import com.grepp.funfun.app.domain.chat.service.ChatRoomService;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.delete.util.CustomCollectors;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
import com.grepp.funfun.app.delete.util.WebUtils;
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
@RequestMapping("/chatRooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final GroupRepository groupRepository;

    public ChatRoomController(final ChatRoomService chatRoomService,
            final GroupRepository groupRepository) {
        this.chatRoomService = chatRoomService;
        this.groupRepository = groupRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("groupValues", groupRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Group::getId, Group::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("chatRooms", chatRoomService.findAll());
        return "chatRoom/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("chatRoom") final ChatRoomDTO chatRoomDTO) {
        return "chatRoom/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("chatRoom") @Valid final ChatRoomDTO chatRoomDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chatRoom/add";
        }
        chatRoomService.create(chatRoomDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chatRoom.create.success"));
        return "redirect:/chatRooms";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("chatRoom", chatRoomService.get(id));
        return "chatRoom/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("chatRoom") @Valid final ChatRoomDTO chatRoomDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chatRoom/edit";
        }
        chatRoomService.update(id, chatRoomDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chatRoom.update.success"));
        return "redirect:/chatRooms";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = chatRoomService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            chatRoomService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("chatRoom.delete.success"));
        }
        return "redirect:/chatRooms";
    }

}
