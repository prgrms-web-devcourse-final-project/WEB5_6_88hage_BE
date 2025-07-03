package com.grepp.funfun.app.controller.web.user;

import com.grepp.funfun.app.controller.api.user.payload.SignupRequest;
import com.grepp.funfun.app.model.auth.code.Role;
import com.grepp.funfun.app.model.user.code.Gender;
import com.grepp.funfun.app.model.user.dto.UserDTO;
import com.grepp.funfun.app.model.user.code.UserStatus;
import com.grepp.funfun.app.model.user.service.UserService;
import com.grepp.funfun.app.model.user.entity.UserInfo;
import com.grepp.funfun.app.model.user.repository.UserInfoRepository;
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
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserInfoRepository userInfoRepository;

    public UserController(final UserService userService,
            final UserInfoRepository userInfoRepository) {
        this.userService = userService;
        this.userInfoRepository = userInfoRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("genderValues", Gender.values());
        model.addAttribute("roleValues", Role.values());
        model.addAttribute("statusValues", UserStatus.values());
        model.addAttribute("infoValues", userInfoRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(UserInfo::getEmail, UserInfo::getEmail)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("user") final UserDTO userDTO) {
        return "user/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("user") @Valid final SignupRequest request,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/add";
        }
        userService.create(request);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.create.success"));
        return "redirect:/users";
    }

    @GetMapping("/edit/{email}")
    public String edit(@PathVariable(name = "email") final String email, final Model model) {
        model.addAttribute("user", userService.get(email));
        return "user/edit";
    }

    @PostMapping("/edit/{email}")
    public String edit(@PathVariable(name = "email") final String email,
            @ModelAttribute("user") @Valid final UserDTO userDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }
        userService.update(email, userDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
        return "redirect:/users";
    }

    @PostMapping("/delete/{email}")
    public String delete(@PathVariable(name = "email") final String email,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(email);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            userService.delete(email);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("user.delete.success"));
        }
        return "redirect:/users";
    }

}
