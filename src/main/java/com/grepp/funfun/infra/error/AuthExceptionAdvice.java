package com.grepp.funfun.infra.error;

import com.grepp.funfun.infra.error.exceptions.AuthApiException;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ApiResponse;
import com.grepp.funfun.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class AuthExceptionAdvice {
    
    @ResponseBody
    @ExceptionHandler(AuthApiException.class)
    public ResponseEntity<ApiResponse<String>> authApiExHandler(
        AuthApiException ex) {
        return ResponseEntity
                   .status(ex.code().status())
                   .body(ApiResponse.error(ex.code()));
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> badCredentialsExHandler(
        BadCredentialsException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(ResponseCode.BAD_CREDENTIAL));
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<String>> authExHandler(
        AuthenticationException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                   .status(HttpStatus.UNAUTHORIZED)
                   .body(ApiResponse.error(ResponseCode.UNAUTHORIZED));
    }

    @ResponseBody
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ApiResponse<String>> commonExHandler(CommonException ex) {
        return ResponseEntity
                .status(ex.code().status())
                .body(ApiResponse.error(ex.code(), ex.getReason()));
    }
}
