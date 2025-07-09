package com.grepp.funfun.app.delete.controller.web.calendar;

import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import com.grepp.funfun.app.domain.calendar.dto.CalendarDTO;
import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
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
@RequestMapping("/calendars")
public class CalendarController {

    private final CalendarService calendarService;
    private final ContentRepository contentRepository;
    private final GroupRepository groupRepository;

    public CalendarController(final CalendarService calendarService,
            final ContentRepository contentRepository, final GroupRepository groupRepository) {
        this.calendarService = calendarService;
        this.contentRepository = contentRepository;
        this.groupRepository = groupRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("typeValues", ActivityType.values());
        model.addAttribute("contentValues", contentRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Content::getId, Content::getId)));
        model.addAttribute("groupValues", groupRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Group::getId, Group::getId)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("calendars", calendarService.findAll());
        return "calendar/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("calendar") final CalendarDTO calendarDTO) {
        return "calendar/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("calendar") @Valid final CalendarDTO calendarDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "calendar/add";
        }
        calendarService.create(calendarDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("calendar.create.success"));
        return "redirect:/calendars";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("calendar", calendarService.get(id));
        return "calendar/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("calendar") @Valid final CalendarDTO calendarDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "calendar/edit";
        }
        calendarService.update(id, calendarDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("calendar.update.success"));
        return "redirect:/calendars";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        calendarService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("calendar.delete.success"));
        return "redirect:/calendars";
    }

}
