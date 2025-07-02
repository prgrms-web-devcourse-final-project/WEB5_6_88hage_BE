package com.grepp.funfun.app.controller.web.contact;

import com.grepp.funfun.app.model.contact.dto.ContactDTO;
import com.grepp.funfun.app.model.contact.code.ContactStatus;
import com.grepp.funfun.app.model.contact.service.ContactService;
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
@RequestMapping("/contacts")
public class ContactController {

    private final ContactService contactService;
    private final UserRepository userRepository;

    public ContactController(final ContactService contactService,
            final UserRepository userRepository) {
        this.contactService = contactService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("statusValues", ContactStatus.values());
        model.addAttribute("userValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("contacts", contactService.findAll());
        return "contact/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("contact") final ContactDTO contactDTO) {
        return "contact/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("contact") @Valid final ContactDTO contactDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contact/add";
        }
        contactService.create(contactDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contact.create.success"));
        return "redirect:/contacts";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("contact", contactService.get(id));
        return "contact/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("contact") @Valid final ContactDTO contactDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "contact/edit";
        }
        contactService.update(id, contactDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("contact.update.success"));
        return "redirect:/contacts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        contactService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("contact.delete.success"));
        return "redirect:/contacts";
    }

}
