package com.grepp.funfun.app.domain.admin.service;

import com.grepp.funfun.app.domain.contact.entity.Contact;
import com.grepp.funfun.app.domain.contact.repository.ContactRepository;
import com.grepp.funfun.app.domain.contact.vo.ContactStatus;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class AdminContactAnswerService {

    private final ContactRepository contactRepository;

    @Transactional
    public void answerContact(Long contactId, String answer) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "존재하지 않는 문의입니다."));

        if (contact.getStatus() == ContactStatus.COMPLETE) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "이미 답변이 완료된 문의입니다.");
        }

        contact.registAnswer(answer);
    }

    @Transactional
    public void delete(Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "존재하지 않는 문의입니다."));

        if (!contact.getActivated()) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "이미 삭제된 문의입니다.");
        }

        contact.unActivated();
    }
}

