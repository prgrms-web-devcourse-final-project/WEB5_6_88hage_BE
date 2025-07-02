package com.grepp.funfun.app.controller.web.preference;

import com.grepp.funfun.app.model.content.code.ContentClassification;
import com.grepp.funfun.app.model.preference.dto.ContentPreferenceDTO;
import com.grepp.funfun.app.model.preference.service.ContentPreferenceService;
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
@RequestMapping("/contentPreferences")
public class ContentPreferenceController {

    private final ContentPreferenceService contentPreferenceService;
    private final UserRepository userRepository;

    public ContentPreferenceController(final ContentPreferenceService contentPreferenceService,
            final UserRepository userRepository) {
        this.contentPreferenceService = contentPreferenceService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("categoryValues", ContentClassification.values());
        model.addAttribute("userValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("contentPreferences", contentPreferenceService.findAll());
        return "contentPreference/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("contentPreference") final ContentPreferenceDTO contentPreferenceDTO) {
        return "contentPreference/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("contentPreference") @Valid final ContentPreferenceDTO contentPreferenceDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contentPreference/add";
        }
        contentPreferenceService.create(contentPreferenceDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentPreference.create.success"));
        return "redirect:/contentPreferences";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("contentPreference", contentPreferenceService.get(id));
        return "contentPreference/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("contentPreference") @Valid final ContentPreferenceDTO contentPreferenceDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contentPreference/edit";
        }
        contentPreferenceService.update(id, contentPreferenceDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contentPreference.update.success"));
        return "redirect:/contentPreferences";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        contentPreferenceService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("contentPreference.delete.success"));
        return "redirect:/contentPreferences";
    }

}
