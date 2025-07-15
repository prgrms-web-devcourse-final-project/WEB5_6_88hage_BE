package com.grepp.funfun.app.infra.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DataInitializeService dataInitializeService;

//    @EventListener(ApplicationReadyEvent.class)
//    public void init(){
//        dataInitializeService.initializeVector();
//    }

}
