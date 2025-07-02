package com.grepp.funfun.app.controller.web.content;

import com.grepp.funfun.app.model.content.dto.ContentDTO;
import com.grepp.funfun.app.model.content.service.ContentService;
import com.grepp.funfun.app.model.content.entity.ContentCategory;
import com.grepp.funfun.app.model.content.repository.ContentCategoryRepository;
import com.grepp.funfun.util.CustomCollectors;
import com.grepp.funfun.util.ReferencedWarning;
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
@RequestMapping("/contents")
public class ContentController {

    private final ContentService contentService;
    private final ContentCategoryRepository contentCategoryRepository;

    public ContentController(final ContentService contentService,
            final ContentCategoryRepository contentCategoryRepository) {
        this.contentService = contentService;
        this.contentCategoryRepository = contentCategoryRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("categoryValues", contentCategoryRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(ContentCategory::getId, ContentCategory::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("contents", contentService.findAll());
        return "content/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("content") final ContentDTO contentDTO) {
        return "content/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("content") @Valid final ContentDTO contentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "content/add";
        }
        contentService.create(contentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("content.create.success"));
        return "redirect:/contents";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("content", contentService.get(id));
        return "content/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("content") @Valid final ContentDTO contentDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "content/edit";
        }
        contentService.update(id, contentDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("content.update.success"));
        return "redirect:/contents";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = contentService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            contentService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("content.delete.success"));
        }
        return "redirect:/contents";
    }

}
