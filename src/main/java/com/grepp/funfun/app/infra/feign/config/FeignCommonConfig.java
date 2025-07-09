package com.grepp.funfun.app.infra.feign.config;

import feign.Request.Options;
import feign.RequestInterceptor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FeignCommonConfig {

    @Bean
    public Options requestOptions(){
        return new Options(5, TimeUnit.SECONDS, 10, TimeUnit.SECONDS, true);
    }
    
    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            log.info("=========================================================");
            log.info("requestTemplate : {}", requestTemplate);
        };
    }

}
