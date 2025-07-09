package com.grepp.funfun.app.domain.preference.service;

import com.grepp.funfun.app.domain.preference.dto.ContentPreferenceDTO;
import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import com.grepp.funfun.app.domain.preference.repository.ContentPreferenceRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ContentPreferenceService {

    private final ContentPreferenceRepository contentPreferenceRepository;
    private final UserRepository userRepository;

    public ContentPreferenceService(final ContentPreferenceRepository contentPreferenceRepository,
            final UserRepository userRepository) {
        this.contentPreferenceRepository = contentPreferenceRepository;
        this.userRepository = userRepository;
    }

    public List<ContentPreferenceDTO> findAll() {
        final List<ContentPreference> contentPreferences = contentPreferenceRepository.findAll(Sort.by("id"));
        return contentPreferences.stream()
                .map(contentPreference -> mapToDTO(contentPreference, new ContentPreferenceDTO()))
                .toList();
    }

    public ContentPreferenceDTO get(final Long id) {
        return contentPreferenceRepository.findById(id)
                .map(contentPreference -> mapToDTO(contentPreference, new ContentPreferenceDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final ContentPreferenceDTO contentPreferenceDTO) {
        final ContentPreference contentPreference = new ContentPreference();
        mapToEntity(contentPreferenceDTO, contentPreference);
        return contentPreferenceRepository.save(contentPreference).getId();
    }

    public void update(final Long id, final ContentPreferenceDTO contentPreferenceDTO) {
        final ContentPreference contentPreference = contentPreferenceRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(contentPreferenceDTO, contentPreference);
        contentPreferenceRepository.save(contentPreference);
    }

    public void delete(final Long id) {
        contentPreferenceRepository.deleteById(id);
    }

    private ContentPreferenceDTO mapToDTO(final ContentPreference contentPreference,
            final ContentPreferenceDTO contentPreferenceDTO) {
        contentPreferenceDTO.setId(contentPreference.getId());
        contentPreferenceDTO.setCategory(contentPreference.getCategory());
        contentPreferenceDTO.setUser(contentPreference.getUser() == null ? null : contentPreference.getUser().getEmail());
        return contentPreferenceDTO;
    }

    private ContentPreference mapToEntity(final ContentPreferenceDTO contentPreferenceDTO,
            final ContentPreference contentPreference) {
        contentPreference.setCategory(contentPreferenceDTO.getCategory());
        final User user = contentPreferenceDTO.getUser() == null ? null : userRepository.findById(contentPreferenceDTO.getUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        contentPreference.setUser(user);
        return contentPreference;
    }

}
