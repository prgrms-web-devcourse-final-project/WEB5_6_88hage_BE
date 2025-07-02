package com.grepp.funfun.app.controller.web.group;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.app.model.group.dto.GroupHashTagDTO;
import com.grepp.funfun.app.model.group.service.GroupHashTagService;
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
@RequestMapping("/groupHashTags")
public class GroupHashTagController {

    private final GroupHashTagService groupHashTagService;
    private final GroupRepository groupRepository;

    public GroupHashTagController(final GroupHashTagService groupHashTagService,
            final GroupRepository groupRepository) {
        this.groupHashTagService = groupHashTagService;
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
        model.addAttribute("groupHashTags", groupHashTagService.findAll());
        return "groupHashTag/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("groupHashTag") final GroupHashTagDTO groupHashTagDTO) {
        return "groupHashTag/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("groupHashTag") @Valid final GroupHashTagDTO groupHashTagDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupHashTag/add";
        }
        groupHashTagService.create(groupHashTagDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupHashTag.create.success"));
        return "redirect:/groupHashTags";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("groupHashTag", groupHashTagService.get(id));
        return "groupHashTag/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("groupHashTag") @Valid final GroupHashTagDTO groupHashTagDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupHashTag/edit";
        }
        groupHashTagService.update(id, groupHashTagDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupHashTag.update.success"));
        return "redirect:/groupHashTags";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        groupHashTagService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupHashTag.delete.success"));
        return "redirect:/groupHashTags";
    }

}
