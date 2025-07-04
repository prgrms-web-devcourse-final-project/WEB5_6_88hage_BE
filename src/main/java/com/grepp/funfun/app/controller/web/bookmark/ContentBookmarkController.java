package com.grepp.funfun.app.controller.web.bookmark;

import com.grepp.funfun.app.model.content.entity.Content;
import com.grepp.funfun.app.model.content.repository.ContentRepository;
import com.grepp.funfun.app.model.bookmark.dto.ContentBookmarkDTO;
import com.grepp.funfun.app.model.bookmark.service.ContentBookmarkService;
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
@RequestMapping("/contentBookmarks")
public class ContentBookmarkController {

    private final ContentBookmarkService contentBookmarkService;
    private final ContentRepository contentRepository;

    public ContentBookmarkController(final ContentBookmarkService contentBookmarkService,
            final ContentRepository contentRepository) {
        this.contentBookmarkService = contentBookmarkService;
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
        model.addAttribute("contentBookmarks", contentBookmarkService.findAll());
        return "contentBookmark/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("contentBookmark") final ContentBookmarkDTO contentBookmarkDTO) {
        return "contentBookmark/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("contentBookmark") @Valid final ContentBookmarkDTO contentBookmarkDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contentBookmark/add";
        }
        contentBookmarkService.add(contentBookmarkDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentBookmark.create.success"));
        return "redirect:/contentBookmarks";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("contentBookmark", contentBookmarkService.get(id));
        return "contentBookmark/edit";
    }

//    @PostMapping("/edit/{id}")
//    public String edit(@PathVariable(name = "id") final Long id,
//            @ModelAttribute("contentBookmark") @Valid final ContentBookmarkDTO contentBookmarkDTO,
//            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "contentBookmark/edit";
//        }
//        contentBookmarkService.update(id, contentBookmarkDTO);
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentBookmark.update.success"));
//        return "redirect:/contentBookmarks";
//    }

//    @PostMapping("/delete/{id}")
//    public String delete(@PathVariable(name = "id") final Long id,
//            final RedirectAttributes redirectAttributes) {
//        contentBookmarkService.delete(id, email);
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("contentBookmark.delete.success"));
//        return "redirect:/contentBookmarks";
//    }

}
