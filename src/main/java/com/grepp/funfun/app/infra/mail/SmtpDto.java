package com.grepp.funfun.app.infra.mail;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class SmtpDto {
    private String templatePath;
    private String to;
    private String from;
    private String subject;
    // thymeleaf 의 context 에 전달할 데이터 저장
    private Map<String, Object> properties = new LinkedHashMap<>();
}
