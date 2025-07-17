package com.grepp.funfun.app.domain.calendar.controller;

import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarContentRequest;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarContentResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarDailyResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarMonthlyResponse;
import com.grepp.funfun.app.domain.calendar.dto.payload.CalendarUpdateRequest;
import com.grepp.funfun.app.domain.calendar.service.CalendarService;
import com.grepp.funfun.app.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @Operation(summary = "캘린더 일정 등록", description = "선택한 컨텐츠 일정을 캘린더에 등록합니다.")
    public ResponseEntity<ApiResponse<String>> addCalendar(
        @RequestBody @Valid CalendarContentRequest request,
        Authentication authentication) {
        String email = authentication.getName();
        calendarService.addContentCalendar(email, request);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 등록되었습니다."));
    }

    @DeleteMapping("/{calendarId}")
    @Operation(summary = "캘린더 일정 삭제", description = "선택한 컨텐츠 일정을 캘린더에서 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteCalendar(@PathVariable Long calendarId,
        Authentication authentication) {
        String email = authentication.getName();
        calendarService.deleteContentCalendar(calendarId, email);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 삭제되었습니다."));
    }

    @PatchMapping("/{calendarId}")
    @Operation(summary = "캘린더 일정 수정", description = "선택한 컨텐츠 일정을 수정합니다.")
    public ResponseEntity<ApiResponse<String>> updateCalendar(@PathVariable Long calendarId,
        @RequestBody @Valid CalendarUpdateRequest request,
        Authentication authentication) {
        String email = authentication.getName();
        calendarService.updateContentCalendar(calendarId, request.getSelectedDate(), email);
        return ResponseEntity.ok(ApiResponse.success("일정이 성공적으로 수정되었습니다."));
    }

    @GetMapping("/monthly")
    @Operation(summary = "월별 일정 조회", description = "선택한 년도와 월에 해당하는 모든 일정을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CalendarMonthlyResponse>>> getMonthlyCalendar(
        @RequestParam int year,
        @RequestParam int month,
        Authentication authentication) {
        String email = authentication.getName();
        YearMonth yearMonth = YearMonth.of(year, month);
        List<CalendarMonthlyResponse> result = calendarService.getMonthly(email, yearMonth);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/daily")
    @Operation(summary = "일별 일정 조회", description = "선택한 날짜로 일정을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CalendarDailyResponse>>> getDailyCalendar(
        @RequestParam int year,
        @RequestParam int month,
        @RequestParam int day,
        Authentication authentication) {
        String email = authentication.getName();
        LocalDate date = LocalDate.of(year, month, day);
        List<CalendarDailyResponse> result = calendarService.getDaily(email, date);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/daily/content")
    @Operation(summary = "컨텐츠 일별 일정 조회", description = "선택한 날짜로 컨텐츠 일정만 조회합니다.")
    public ResponseEntity<ApiResponse<List<CalendarDailyResponse>>> getDailyCalendarForContent(
        @RequestParam int year,
        @RequestParam int month,
        @RequestParam int day,
        Authentication authentication) {
        String email = authentication.getName();
        LocalDate date = LocalDate.of(year, month, day);
        List<CalendarDailyResponse> result = calendarService.getDailyForContent(email, date);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/content")
    @Operation(summary = "일정 등록한 컨텐츠 목록 조회", description = """
        캘린더에 일정 등록한 컨텐츠 목록을 조회합니다.
        
        기본 정렬은 등록한 일정 날짜 (selectedDate) 최신순입니다.
        
        • pastIncluded: 지나간 일정 포함 여부 (기본값 true)
        
            - false 면 오늘 00시 이후 일정만 조회됩니다.
        
        • page: 0 ~ N, 보고 싶은 페이지를 지정할 수 있습니다.
       
            - 기본값: 0
        
        • size: 기본 페이지당 항목 수
        
            - 기본값 : 10
        
        • sort: 정렬
        
            - 정렬 가능한 필드:
                        - `selectedDate` (등록한 일정 날짜)
        
            - 정렬 방식 예시:
                        - `?sort=selectedDate,desc` (기본값, 최신순)
                        - `?sort=selectedDate,asc` (오래된순)
        """)
    public ResponseEntity<ApiResponse<Page<CalendarContentResponse>>> getCalendarForContent(
        Authentication authentication,
        @Parameter(description = "지나간 일정 포함 여부")
        @RequestParam(defaultValue = "true") boolean pastIncluded,
        @Parameter(hidden = true)
        @ParameterObject
        @PageableDefault(sort = "selectedDate", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success(calendarService.getContent(email, pastIncluded, pageable)));
    }
}
