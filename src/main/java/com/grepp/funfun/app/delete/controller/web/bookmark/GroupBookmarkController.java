package com.grepp.funfun.app.delete.controller.web.bookmark;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.bookmark.dto.GroupBookmarkDTO;
import com.grepp.funfun.app.domain.bookmark.service.GroupBookmarkService;
import com.grepp.funfun.app.delete.util.CustomCollectors;
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
@RequestMapping("/groupBookmarks")
public class GroupBookmarkController {

    private final GroupBookmarkService groupBookmarkService;
    private final GroupRepository groupRepository;

    public GroupBookmarkController(final GroupBookmarkService groupBookmarkService,
            final GroupRepository groupRepository) {
        this.groupBookmarkService = groupBookmarkService;
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
        model.addAttribute("groupBookmarks", groupBookmarkService.findAll());
        return "groupBookmark/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("groupBookmark") final GroupBookmarkDTO groupBookmarkDTO) {
        return "groupBookmark/add";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("groupBookmark", groupBookmarkService.get(id));
        return "groupBookmark/edit";
    }


    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        groupBookmarkService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupBookmark.delete.success"));
        return "redirect:/groupBookmarks";
    }

}
