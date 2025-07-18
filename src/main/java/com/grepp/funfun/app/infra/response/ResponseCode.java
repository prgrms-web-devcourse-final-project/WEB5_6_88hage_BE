package com.grepp.funfun.app.infra.response;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    OK("0000", HttpStatus.OK, "정상적으로 완료되었습니다."),
    BAD_REQUEST("4000", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_FILENAME("4001", HttpStatus.BAD_REQUEST, "사용 할 수 없는 파일 이름입니다."),
    UNAUTHORIZED("4010", HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    BAD_CREDENTIAL("4011", HttpStatus.UNAUTHORIZED, "아이디나 비밀번호가 틀렸습니다."),
    NOT_FOUND("4040", HttpStatus.NOT_FOUND, "NOT FOUND"),
    NOT_EXIST_PRE_AUTH_CREDENTIAL("4012", HttpStatus.OK, "사전 인증 정보가 요청에서 발견되지 않았습니다."),
    USER_EMAIL_DUPLICATE("4013", HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    USER_NICKNAME_DUPLICATE("4014", HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
    BAD_USER_VERIFY("4015", HttpStatus.BAD_REQUEST, "인증 링크가 만료되었거나 잘못되었습니다."),
    ALREADY_VERIFIED("4016", HttpStatus.BAD_REQUEST, "이미 인증된 사용자입니다."),
    USER_NOT_VERIFY("4017", HttpStatus.BAD_REQUEST, "이메일 인증이 되지 않은 사용자입니다"),
    USER_SUSPENDED("4018", HttpStatus.BAD_REQUEST, "일시정지된 사용자입니다."),
    USER_BANNED("4022", HttpStatus.BAD_REQUEST, "영구정지된 사용자입니다."),
    USER_INACTIVE("4019", HttpStatus.BAD_REQUEST, "비활성화된 사용자입니다."),
    INVALID_AUTH_CODE("4020", HttpStatus.BAD_REQUEST, "잘못된 인증 코드입니다."),
    TOO_FAST_VERIFY_REQUEST("4021", HttpStatus.BAD_REQUEST, "인증 메일은 3분마다 발송할 수 있습니다."),
    INTERNAL_SERVER_ERROR("5000", HttpStatus.INTERNAL_SERVER_ERROR, "서버에러 입니다."),
    SECURITY_INCIDENT("6000", HttpStatus.OK, "비정상적인 로그인 시도가 감지되었습니다.");
    
    private final String code;
    private final HttpStatus status;
    private final String message;
    
    ResponseCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
    
    public String code() {
        return code;
    }
    
    public HttpStatus status() {
        return status;
    }
    
    public String message() {
        return message;
    }
}
