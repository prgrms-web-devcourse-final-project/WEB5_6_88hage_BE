package com.grepp.funfun.app.domain.faq.service;

import com.grepp.funfun.app.domain.faq.dto.payload.FaqCreateRequest;
import com.grepp.funfun.app.domain.faq.dto.payload.FaqUpdateRequest;
import com.grepp.funfun.app.domain.faq.dto.FaqDTO;
import com.grepp.funfun.app.domain.faq.entity.Faq;
import com.grepp.funfun.app.domain.faq.mapper.FaqMapper;
import com.grepp.funfun.app.domain.faq.repository.FaqRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FaqService {
    private final FaqRepository faqRepository;
    private final FaqMapper faqMapper;

    public FaqService(FaqRepository faqRepository, FaqMapper faqMapper) {
        this.faqRepository = faqRepository;
        this.faqMapper = faqMapper;
    }

    public List<FaqDTO> findAll(){
        final List<Faq> faqs =  faqRepository.findAllByActivatedTrue();
        return faqs.stream()
                .map(faqMapper::toDto)
                .toList();
    }

    public FaqDTO get(final Long id) {
        final Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
                return faqMapper.toDto(faq);
    }

    // Api
    public Long createFromRequest(final FaqCreateRequest request) {
        Faq faq = new Faq();
        faq.setTitle(request.getTitle());
        faq.setContent(request.getContent());
        return faqRepository.save(faq).getId();
    }

    public void updateFromRequest(final Long id, final FaqUpdateRequest request) {
        Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        faq.setTitle(request.getTitle());
        faq.setContent(request.getContent());
        faqRepository.save(faq);
    }

    public Long create(final FaqDTO faqDTO) {
        final Faq faq = faqMapper.toEntity(faqDTO);
        return faqRepository.save(faq).getId();
    }

    public void update(final Long id, final FaqDTO faqDTO) {
        final Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        faqMapper.updateEntity(faqDTO, faq);
        faqRepository.save(faq);
    }

    public void delete(final Long id) {
        final Faq faq = faqRepository.findById(id)
                .filter(Faq::getActivated)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
            faq.unActivated();
            faqRepository.save(faq);
    }
}
