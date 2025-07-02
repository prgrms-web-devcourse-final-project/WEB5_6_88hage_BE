package com.grepp.funfun.app.controller.web.recommend;

import com.grepp.funfun.app.model.calendar.code.ActivityType;
import com.grepp.funfun.app.model.recommend.dto.ChatBotDTO;
import com.grepp.funfun.app.model.recommend.service.ChatBotService;
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
@RequestMapping("/chatBots")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(final ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("typeValues", ActivityType.values());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("chatBots", chatBotService.findAll());
        return "chatBot/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("chatBot") final ChatBotDTO chatBotDTO) {
        return "chatBot/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("chatBot") @Valid final ChatBotDTO chatBotDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chatBot/add";
        }
        chatBotService.create(chatBotDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chatBot.create.success"));
        return "redirect:/chatBots";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("chatBot", chatBotService.get(id));
        return "chatBot/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("chatBot") @Valid final ChatBotDTO chatBotDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "chatBot/edit";
        }
        chatBotService.update(id, chatBotDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("chatBot.update.success"));
        return "redirect:/chatBots";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        chatBotService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("chatBot.delete.success"));
        return "redirect:/chatBots";
    }

}
