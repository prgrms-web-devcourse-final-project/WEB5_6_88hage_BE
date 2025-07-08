package com.grepp.funfun.app.controller.api.calendar;

import com.grepp.funfun.app.controller.api.calendar.payload.CalendarContentRequest;
import com.grepp.funfun.app.controller.api.calendar.payload.CalendarMonthlyResponse;
import com.grepp.funfun.app.controller.api.calendar.payload.CalendarUpdateRequest;
import com.grepp.funfun.app.model.calendar.service.CalendarService;
import com.grepp.funfun.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/calendars", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CalendarApiController {

    private final CalendarService calendarService;

    @PostMapping
    @Operation(summary = "캘린더 일정 등록", description = "선택한 Content 일정을 캘린더에 등록합니다.")
    public ResponseEntity<ApiResponse<String>> addCalendar(@RequestBody @Valid CalendarContentRequest request,
        Authentication authentication) {
        String email = authentication.getName();
        calendarService.addCalendar(email, request);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 등록되었습니다."));
    }

    @DeleteMapping("/{calendarId}")
    @Operation(summary = "캘린더 일정 삭제", description = "선택한 Content 일정을 캘린더에서 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteCalendar(@PathVariable Long calendarId,
        Authentication authentication) {
        String email = authentication.getName();
        calendarService.deleteCalendar(calendarId, email);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 삭제되었습니다."));
    }

    @PatchMapping("/{calendarId}")
    @Operation(summary = "캘린더 일정 수정", description = "선택한 Content 일정을 수정합니다. (Group 타입은 수정 불가)")
    public ResponseEntity<ApiResponse<String>> updateCalendar(@PathVariable Long calendarId,
        @RequestBody @Valid CalendarUpdateRequest request,
        Authentication authentication) {
        String email = authentication.getName();
        calendarService.updateCalendar(calendarId, request.getSelectedDate(), email);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 수정되었습니다."));
    }

    @GetMapping("/monthly")
    @Operation(summary = "월별 일정 조회", description = "선택한 년도와 월에 해당하는 모든 일정을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CalendarMonthlyResponse>>> getMonthlyCalendar(@RequestParam int year,
        @RequestParam int month,
        Authentication authentication) {
        String email = authentication.getName();
        YearMonth yearMonth = YearMonth.of(year, month);
        List<CalendarMonthlyResponse> result = calendarService.getMonthly(email, yearMonth);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
