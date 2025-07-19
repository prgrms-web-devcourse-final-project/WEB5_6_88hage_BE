package com.grepp.funfun.app.domain.notification.sse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(String email, SseEmitter emitter) {
        emitters.put(email, emitter);
        return emitter;
    }

    public SseEmitter get(String email) {
        return emitters.get(email);
    }

    public void delete(String email) {
        emitters.remove(email);
    }
}
