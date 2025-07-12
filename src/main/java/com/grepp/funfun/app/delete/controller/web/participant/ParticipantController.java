package com.grepp.funfun.app.delete.controller.web.participant;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.participant.dto.ParticipantDTO;
import com.grepp.funfun.app.domain.participant.dto.payload.ParticipantResponse;
import com.grepp.funfun.app.domain.participant.vo.ParticipantRole;
import com.grepp.funfun.app.domain.participant.vo.ParticipantStatus;
import com.grepp.funfun.app.domain.participant.service.ParticipantService;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.delete.util.CustomCollectors;
import com.grepp.funfun.app.delete.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/participants")
public class ParticipantController {

    private final ParticipantService participantService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;


    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("roleValues", ParticipantRole.values());
        model.addAttribute("statusValues", ParticipantStatus.values());
        model.addAttribute("userValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
        model.addAttribute("groupValues", groupRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Group::getId, Group::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("participants", participantService.findAll());
        return "participant/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("participant") final ParticipantDTO participantDTO) {
        return "participant/add";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("participant", participantService.get(id));
        return "participant/edit";
    }
}
