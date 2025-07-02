package com.grepp.funfun.app.controller.api.calendar;

import com.grepp.funfun.app.model.calendar.dto.CalendarDTO;
import com.grepp.funfun.app.model.calendar.service.CalendarService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/calendars", produces = MediaType.APPLICATION_JSON_VALUE)
public class CalendarApiController {

    private final CalendarService calendarService;

    public CalendarApiController(final CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public ResponseEntity<List<CalendarDTO>> getAllCalendars() {
        return ResponseEntity.ok(calendarService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarDTO> getCalendar(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(calendarService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCalendar(@RequestBody @Valid final CalendarDTO calendarDTO) {
        final Long createdId = calendarService.create(calendarDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateCalendar(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final CalendarDTO calendarDTO) {
        calendarService.update(id, calendarDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCalendar(@PathVariable(name = "id") final Long id) {
        calendarService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
