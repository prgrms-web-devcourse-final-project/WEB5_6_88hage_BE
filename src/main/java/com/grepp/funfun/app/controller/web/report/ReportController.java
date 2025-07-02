package com.grepp.funfun.app.controller.web.report;

import com.grepp.funfun.app.model.report.dto.ReportDTO;
import com.grepp.funfun.app.model.report.code.ReportType;
import com.grepp.funfun.app.model.report.service.ReportService;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
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
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportController(final ReportService reportService,
            final UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("typeValues", ReportType.values());
        model.addAttribute("reportingUserValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
        model.addAttribute("reportedUserValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("reports", reportService.findAll());
        return "report/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("report") final ReportDTO reportDTO) {
        return "report/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("report") @Valid final ReportDTO reportDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "report/add";
        }
        reportService.create(reportDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("report.create.success"));
        return "redirect:/reports";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("report", reportService.get(id));
        return "report/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("report") @Valid final ReportDTO reportDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "report/edit";
        }
        reportService.update(id, reportDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("report.update.success"));
        return "redirect:/reports";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        reportService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("report.delete.success"));
        return "redirect:/reports";
    }

}
