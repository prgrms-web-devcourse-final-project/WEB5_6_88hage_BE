package com.grepp.funfun.app.delete.controller.web.admin;

import com.grepp.funfun.app.domain.admin.dto.NoticeDTO;
import com.grepp.funfun.app.domain.admin.service.NoticeService;
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
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(final NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("notices", noticeService.findAll());
        return "notice/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("notice") final NoticeDTO noticeDTO) {
        return "notice/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("notice") @Valid final NoticeDTO noticeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "notice/add";
        }
        noticeService.create(noticeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("notice.create.success"));
        return "redirect:/notices";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("notice", noticeService.get(id));
        return "notice/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("notice") @Valid final NoticeDTO noticeDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "notice/edit";
        }
        noticeService.update(id, noticeDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("notice.update.success"));
        return "redirect:/notices";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        noticeService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("notice.delete.success"));
        return "redirect:/notices";
    }

}
