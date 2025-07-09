package com.grepp.funfun.app.delete.controller.web.social;

import com.grepp.funfun.app.domain.social.dto.FollowDTO;
import com.grepp.funfun.app.domain.social.service.FollowService;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.delete.util.CustomCollectors;
import com.grepp.funfun.app.delete.util.WebUtils;
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
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;
    private final UserRepository userRepository;

    public FollowController(final FollowService followService,
            final UserRepository userRepository) {
        this.followService = followService;
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("followerValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
        model.addAttribute("followeeValues", userRepository.findAll(Sort.by("email"))
                .stream()
                .collect(CustomCollectors.toSortedMap(User::getEmail, User::getPassword)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("follows", followService.findAll());
        return "follow/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("follow") final FollowDTO followDTO) {
        return "follow/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("follow") @Valid final FollowDTO followDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "follow/add";
        }
        followService.create(followDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("follow.create.success"));
        return "redirect:/follows";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("follow", followService.get(id));
        return "follow/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("follow") @Valid final FollowDTO followDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "follow/edit";
        }
        followService.update(id, followDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("follow.update.success"));
        return "redirect:/follows";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        followService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("follow.delete.success"));
        return "redirect:/follows";
    }

}
