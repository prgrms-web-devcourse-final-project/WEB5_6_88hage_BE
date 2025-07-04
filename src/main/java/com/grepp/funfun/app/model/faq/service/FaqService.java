package com.grepp.funfun.app.model.faq.service;

import com.grepp.funfun.app.controller.api.faq.payload.FaqCreateRequest;
import com.grepp.funfun.app.controller.api.faq.payload.FaqUpdateRequest;
import com.grepp.funfun.app.model.faq.dto.FaqDTO;
import com.grepp.funfun.app.model.faq.entity.Faq;
import com.grepp.funfun.app.model.faq.repository.FaqRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqService {
    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<FaqDTO> findAll(){
        final List<Faq> faqs =  faqRepository.findAllByActivatedTrue();
        return faqs.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public FaqDTO get(final Long id) {
        final Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
                return mapToDTO(faq);
    }

    // Api
    public Long createFromRequest(final FaqCreateRequest request) {
        Faq faq = new Faq();
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        return faqRepository.save(faq).getId();
    }

    public void updateFromRequest(final Long id, final FaqUpdateRequest request) {
        Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faqRepository.save(faq);
    }

    // Web
    public Long create(final FaqDTO faqDTO) {
        final Faq faq = mapToEntity(faqDTO, new Faq());
        return faqRepository.save(faq).getId();
    }

    public void update(final Long id, final FaqDTO faqDTO) {
        final Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
            mapToEntity(faqDTO,faq);
            faqRepository.save(faq);
    }

    public void delete(final Long id) {
        final Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
            faq.unActivated();
            faqRepository.save(faq);
    }

    private FaqDTO mapToDTO(final Faq faq) {
        final FaqDTO dto = new FaqDTO();
        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        return dto;
    }

    private Faq mapToEntity(final FaqDTO dto, final Faq faq) {
        faq.setQuestion(dto.getQuestion());
        faq.setAnswer(dto.getAnswer());
        return faq;
    }
}
