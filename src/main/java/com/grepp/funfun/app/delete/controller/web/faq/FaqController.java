package com.grepp.funfun.app.delete.controller.web.faq;

import com.grepp.funfun.app.domain.faq.dto.FaqDTO;
import com.grepp.funfun.app.domain.faq.service.FaqService;
import com.grepp.funfun.app.delete.util.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/faqs")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("faqs", faqService.findAll());
        return "faq/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("faq") FaqDTO faqDTO){
        return "faq/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("faq") @Valid FaqDTO faqDTO,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()) {
            return "faq/add";
        }
        faqService.create(faqDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("faq.create.success"));
            return "redirect:/faqs";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("faq", faqService.get(id));
        return "faq/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute("faq") @Valid FaqDTO faqDTO,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) {
            return "faq/edit";
        }
        faqService.update(id, faqDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("faq.update.success"));
        return "redirect:/faqs";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        faqService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                WebUtils.getMessage("faq.delete.success"));
        return "redirect:/faqs";
    }
}
