package com.grepp.funfun.app.controller.web.group;

import com.grepp.funfun.app.model.group.entity.Group;
import com.grepp.funfun.app.model.group.repository.GroupRepository;
import com.grepp.funfun.app.model.group.dto.GroupHashtagDTO;
import com.grepp.funfun.app.model.group.service.GroupHashtagService;
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
@RequestMapping("/groupHashtags")
public class GroupHashtagController {

    private final GroupHashtagService groupHashtagService;
    private final GroupRepository groupRepository;

    public GroupHashtagController(final GroupHashtagService groupHashtagService,
            final GroupRepository groupRepository) {
        this.groupHashtagService = groupHashtagService;
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
        model.addAttribute("groupHashtags", groupHashtagService.findAll());
        return "groupHashtag/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("groupHashtag") final GroupHashtagDTO groupHashtagDTO) {
        return "groupHashtag/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("groupHashtag") @Valid final GroupHashtagDTO groupHashtagDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupHashtag/add";
        }
        groupHashtagService.create(groupHashtagDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupHashtag.create.success"));
        return "redirect:/groupHashtags";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("groupHashtag", groupHashtagService.get(id));
        return "groupHashtag/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("groupHashtag") @Valid final GroupHashtagDTO groupHashtagDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupHashtag/edit";
        }
        groupHashtagService.update(id, groupHashtagDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupHashtag.update.success"));
        return "redirect:/groupHashtags";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        groupHashtagService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupHashtag.delete.success"));
        return "redirect:/groupHashtags";
    }

}
