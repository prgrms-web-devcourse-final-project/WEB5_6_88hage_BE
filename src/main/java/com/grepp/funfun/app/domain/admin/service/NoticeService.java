package com.grepp.funfun.app.domain.admin.service;

import com.grepp.funfun.app.domain.admin.dto.NoticeDTO;
import com.grepp.funfun.app.domain.admin.entity.Notice;
import com.grepp.funfun.app.domain.admin.repository.NoticeRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(final NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<NoticeDTO> findAll() {
        final List<Notice> notices = noticeRepository.findAll(Sort.by("id"));
        return notices.stream()
                .map(notice -> mapToDTO(notice, new NoticeDTO()))
                .toList();
    }

    public NoticeDTO get(final Long id) {
        return noticeRepository.findById(id)
                .map(notice -> mapToDTO(notice, new NoticeDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final NoticeDTO noticeDTO) {
        final Notice notice = new Notice();
        mapToEntity(noticeDTO, notice);
        return noticeRepository.save(notice).getId();
    }

    public void update(final Long id, final NoticeDTO noticeDTO) {
        final Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(noticeDTO, notice);
        noticeRepository.save(notice);
    }

    public void delete(final Long id) {
        noticeRepository.deleteById(id);
    }

    private NoticeDTO mapToDTO(final Notice notice, final NoticeDTO noticeDTO) {
        noticeDTO.setId(notice.getId());
        noticeDTO.setMessage(notice.getMessage());
        return noticeDTO;
    }

    private Notice mapToEntity(final NoticeDTO noticeDTO, final Notice notice) {
        notice.setMessage(noticeDTO.getMessage());
        return notice;
    }

}
