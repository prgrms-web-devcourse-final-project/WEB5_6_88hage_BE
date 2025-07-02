package com.grepp.funfun.app.controller.web.user;

import com.grepp.funfun.app.model.user.dto.UserInfoDTO;
import com.grepp.funfun.app.model.user.service.UserInfoService;
import com.grepp.funfun.util.ReferencedWarning;
import com.grepp.funfun.util.WebUtils;
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
@RequestMapping("/userInfos")
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(final UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("userInfoes", userInfoService.findAll());
        return "userInfo/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("userInfo") final UserInfoDTO userInfoDTO) {
        return "userInfo/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("userInfo") @Valid final UserInfoDTO userInfoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "userInfo/add";
        }
        userInfoService.create(userInfoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("userInfo.create.success"));
        return "redirect:/userInfos";
    }

    @GetMapping("/edit/{email}")
    public String edit(@PathVariable(name = "email") final String email, final Model model) {
        model.addAttribute("userInfo", userInfoService.get(email));
        return "userInfo/edit";
    }

    @PostMapping("/edit/{email}")
    public String edit(@PathVariable(name = "email") final String email,
            @ModelAttribute("userInfo") @Valid final UserInfoDTO userInfoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "userInfo/edit";
        }
        userInfoService.update(email, userInfoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("userInfo.update.success"));
        return "redirect:/userInfos";
    }

    @PostMapping("/delete/{email}")
    public String delete(@PathVariable(name = "email") final String email,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = userInfoService.getReferencedWarning(email);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            userInfoService.delete(email);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("userInfo.delete.success"));
        }
        return "redirect:/userInfos";
    }

}
