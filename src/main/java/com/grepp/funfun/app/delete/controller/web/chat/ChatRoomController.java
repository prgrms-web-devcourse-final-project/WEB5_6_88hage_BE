package com.grepp.funfun.app.delete.controller.web.chat;

import com.grepp.funfun.app.domain.chat.dto.ChatRoomDTO;
import com.grepp.funfun.app.domain.chat.service.ChatRoomService;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.delete.util.CustomCollectors;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
import com.grepp.funfun.app.delete.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ChatRoomController {

    private final GroupRepository groupRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("groupValues", groupRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Group::getId, Group::getId)));
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("chatRoom") final ChatRoomDTO chatRoomDTO) {
        return "chatRoom/add";
    }


}
