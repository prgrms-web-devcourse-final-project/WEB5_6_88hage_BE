package com.grepp.funfun.app.controller.web.preference;

import com.grepp.funfun.app.model.group.code.GroupClassification;
import com.grepp.funfun.app.model.preference.dto.GroupPreferenceDTO;
import com.grepp.funfun.app.model.preference.service.GroupPreferenceService;
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
@RequestMapping("/groupPreferences")
public class GroupPreferenceController {

    private final GroupPreferenceService groupPreferenceService;
    private final UserRepository userRepository;

    public GroupPreferenceController(final GroupPreferenceService groupPreferenceService,
            final UserRepository userRepository) {
        this.groupPreferenceService = groupPreferenceService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("categoryValues", GroupClassification.values());
        model.addAttribute("userValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("groupPreferences", groupPreferenceService.findAll());
        return "groupPreference/list";
    }

    @GetMapping("/add")
    public String add(
            @ModelAttribute("groupPreference") final GroupPreferenceDTO groupPreferenceDTO) {
        return "groupPreference/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("groupPreference") @Valid final GroupPreferenceDTO groupPreferenceDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupPreference/add";
        }
        groupPreferenceService.create(groupPreferenceDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupPreference.create.success"));
        return "redirect:/groupPreferences";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("groupPreference", groupPreferenceService.get(id));
        return "groupPreference/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("groupPreference") @Valid final GroupPreferenceDTO groupPreferenceDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "groupPreference/edit";
        }
        groupPreferenceService.update(id, groupPreferenceDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("groupPreference.update.success"));
        return "redirect:/groupPreferences";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        groupPreferenceService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("groupPreference.delete.success"));
        return "redirect:/groupPreferences";
    }

}
