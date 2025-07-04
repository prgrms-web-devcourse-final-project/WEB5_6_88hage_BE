package com.grepp.funfun.infra.mail;

import jakarta.mail.Message.RecipientType;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
@Setter
@EnableAsync
public class MailTemplate {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void send(SmtpDto dto){
        javaMailSender.send(mimeMessage -> {
            mimeMessage.setFrom(dto.getFrom());
            mimeMessage.addRecipients(RecipientType.TO, dto.getTo());
            mimeMessage.setSubject(dto.getSubject());
            mimeMessage.setText(render(dto), "UTF-8", "html");
        });
    }

    private String render(SmtpDto dto) {
        Context context = new Context();
        context.setVariables(dto.getProperties());
        return templateEngine.process(dto.getTemplatePath(), context);
    }
}
