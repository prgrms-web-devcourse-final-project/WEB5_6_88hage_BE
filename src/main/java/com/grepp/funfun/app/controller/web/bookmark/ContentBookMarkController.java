package com.grepp.funfun.app.controller.web.bookmark;

import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.app.model.bookmark.dto.ContentBookMarkDTO;
import com.grepp.funfun.app.model.bookmark.service.ContentBookMarkService;
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
@RequestMapping("/contentBookMarks")
public class ContentBookMarkController {

    private final ContentBookMarkService contentBookMarkService;
    private final ContentRepository contentRepository;

    public ContentBookMarkController(final ContentBookMarkService contentBookMarkService,
            final ContentRepository contentRepository) {
        this.contentBookMarkService = contentBookMarkService;
        this.contentRepository = contentRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("contentValues", contentRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Content::getId, Content::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("contentBookMarks", contentBookMarkService.findAll());
        return "contentBookMark/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("contentBookMark") final ContentBookMarkDTO contentBookMarkDTO) {
        return "contentBookMark/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("contentBookMark") @Valid final ContentBookMarkDTO contentBookMarkDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contentBookMark/add";
        }
        contentBookMarkService.create(contentBookMarkDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentBookMark.create.success"));
        return "redirect:/contentBookMarks";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("contentBookMark", contentBookMarkService.get(id));
        return "contentBookMark/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("contentBookMark") @Valid final ContentBookMarkDTO contentBookMarkDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contentBookMark/edit";
        }
        contentBookMarkService.update(id, contentBookMarkDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentBookMark.update.success"));
        return "redirect:/contentBookMarks";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        contentBookMarkService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("contentBookMark.delete.success"));
        return "redirect:/contentBookMarks";
    }

}
