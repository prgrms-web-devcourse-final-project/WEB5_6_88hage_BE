package com.grepp.funfun.app.controller.web.group;

import com.grepp.funfun.app.controller.api.group.payload.GroupRequest;
import com.grepp.funfun.app.model.group.code.GroupClassification;
import com.grepp.funfun.app.model.group.code.GroupStatus;
import com.grepp.funfun.app.model.group.service.GroupService;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.util.CustomCollectors;
import com.grepp.funfun.util.ReferencedWarning;
import com.grepp.funfun.util.WebUtils;
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
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("statusValues", GroupStatus.values());
        model.addAttribute("categoryValues", GroupClassification.values());
        model.addAttribute("leaderValues", userRepository.findAll(Sort.by("email"))
            .stream()
            .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("groups", groupService.findByActivatedTrue());
        return "group/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("group") final GroupRequest groupRequest) {
        return "group/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("group") @Valid GroupRequest request,
        final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "group/add";
        }
        String leaderEmail = "test@aaa.aaa";
        groupService.create(leaderEmail,request);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("group.create.success"));
        return "redirect:/groups";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("group", groupService.get(id));
        return "group/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
        @ModelAttribute("group") @Valid final GroupRequest updateRequest, Long groupId, String leaderEmail,
        final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "group/edit";
        }
        groupService.update(groupId, leaderEmail,updateRequest);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("group.update.success"));
        return "redirect:/groups";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long groupId,String leaderEmail,
        final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = groupService.getReferencedWarning(groupId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            groupService.delete(groupId, leaderEmail);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("group.delete.success"));
        }
        return "redirect:/groups";
    }

}

