package com.grepp.funfun.infra.feign.error;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Slf4j
@ToString
public class FeignCommonException extends RuntimeException{

    private String code;
    private String message;
    private HttpStatus status;
    
    public FeignCommonException() {
    }
    
    public FeignCommonException(Throwable cause) {
        super(cause);
    }
    
    public FeignCommonException(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
    
    public FeignCommonException(Throwable cause, String code, String message, HttpStatus status) {
        super(cause);
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
