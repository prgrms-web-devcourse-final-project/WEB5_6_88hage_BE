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
@RequestMapping("/ContentBookmarks")
public class ContentBookmarkController {

    private final ContentBookmarkService ContentBookmarkService;
    private final ContentRepository contentRepository;

    public ContentBookmarkController(final ContentBookmarkService ContentBookmarkService,
                                     final ContentRepository contentRepository) {
        this.ContentBookmarkService = ContentBookmarkService;
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
        model.addAttribute("ContentBookmarks", ContentBookmarkService.findAll());
        return "ContentBookmark/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("ContentBookmark") final ContentBookmarkDTO ContentBookmarkDTO) {
        return "ContentBookmark/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("ContentBookmark") @Valid final ContentBookmarkDTO ContentBookmarkDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "ContentBookmark/add";
        }
        ContentBookmarkService.create(ContentBookmarkDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("ContentBookmark.create.success"));
        return "redirect:/ContentBookmarks";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("ContentBookmark", ContentBookmarkService.get(id));
        return "ContentBookmark/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
                       @ModelAttribute("ContentBookmark") @Valid final ContentBookmarkDTO ContentBookmarkDTO,
                       final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "ContentBookmark/edit";
        }
        ContentBookmarkService.update(id, ContentBookmarkDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("ContentBookmark.update.success"));
        return "redirect:/ContentBookmarks";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
                         final RedirectAttributes redirectAttributes) {
        ContentBookmarkService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("ContentBookmark.delete.success"));
        return "redirect:/ContentBookmarks";
    }

}