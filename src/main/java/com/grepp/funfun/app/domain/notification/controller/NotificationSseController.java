package com.grepp.funfun.app.domain.notification.controller;

import com.grepp.funfun.app.domain.notification.sse.EmitterRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/subscribe")
public class NotificationSseController {

    private final EmitterRepository emitterRepository;

    @Operation(summary = "SSE 구독", description = "실시간 알림을 수신하기 위한 SSE 연결을 생성합니다.")
    @GetMapping("/{email}")
    public SseEmitter subscribe(@PathVariable String email) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 한시간 타임아웃
        emitterRepository.save(email, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(email));
        emitter.onTimeout(() -> emitterRepository.delete(email));

        // 연결 직후 더미 데이터 전송 (브라우저의 EventSource 연결 확인용)
        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE 연결 완료"));
        } catch (IOException e) {
            emitterRepository.delete(email);
        }

        return emitter;
    }
}
