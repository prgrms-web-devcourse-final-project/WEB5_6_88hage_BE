package com.grepp.funfun.infra.error.exceptions;

import com.grepp.funfun.infra.response.ResponseCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CommonException extends RuntimeException {

    private final ResponseCode code;
    private final String reason;

    public CommonException(ResponseCode code) {
        super(code.message());
        this.code = code;
        this.reason = code.message();
    }

    // 사용자에게 reason 띄우기 위함
    public CommonException(ResponseCode code, String reason) {
        super(reason);
        this.code = code;
        this.reason = reason;
        log.error(reason);
    }

    // 내부 예외 wrapping 용도
    public CommonException(ResponseCode code, Exception e) {
        super(e);
        this.code = code;
        this.reason = e.getMessage() != null ? e.getMessage() : code.message();
        log.error(e.getMessage(), e);
    }

// 굳이 logOnly 를 넣어야할까 싶어서 주석처리
//    public CommonException(ResponseCode code, String message, boolean logOnly) {
//        super(message);
//        this.code = code;
//        this.reason = message;
//        if( logOnly ) {
//            log.error(message);
//        }
//    }

    public ResponseCode code() {
        return code;
    }
}
