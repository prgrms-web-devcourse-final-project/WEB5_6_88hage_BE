package com.grepp.funfun.app.delete.controller.web.content;

import com.grepp.funfun.app.domain.content.dto.ContentCategoryDTO;
import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.content.service.ContentCategoryService;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
import com.grepp.funfun.app.delete.util.WebUtils;
import jakarta.validation.Valid;
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
@RequestMapping("/contentCategories")
public class ContentCategoryController {

    private final ContentCategoryService contentCategoryService;

    public ContentCategoryController(final ContentCategoryService contentCategoryService) {
        this.contentCategoryService = contentCategoryService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("categoryValues", ContentClassification.values());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("contentCategories", contentCategoryService.findAll());
        return "contentCategory/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("contentCategory") final ContentCategoryDTO contentCategoryDTO) {
        return "contentCategory/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("contentCategory") @Valid final ContentCategoryDTO contentCategoryDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contentCategory/add";
        }
        contentCategoryService.create(contentCategoryDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentCategory.create.success"));
        return "redirect:/contentCategories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("contentCategory", contentCategoryService.get(id));
        return "contentCategory/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("contentCategory") @Valid final ContentCategoryDTO contentCategoryDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contentCategory/edit";
        }
        contentCategoryService.update(id, contentCategoryDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentCategory.update.success"));
        return "redirect:/contentCategories";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = contentCategoryService.getReferencedWarning(id);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            contentCategoryService.delete(id);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("contentCategory.delete.success"));
        }
        return "redirect:/contentCategories";
    }

}
