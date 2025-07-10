package com.grepp.funfun.app.domain.preference.service;

import com.grepp.funfun.app.domain.preference.dto.GroupPreferenceDTO;
import com.grepp.funfun.app.domain.preference.dto.payload.ContentPreferenceRequest;
import com.grepp.funfun.app.domain.preference.dto.payload.GroupPreferenceRequest;
import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import com.grepp.funfun.app.domain.preference.repository.GroupPreferenceRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GroupPreferenceService {

    private final GroupPreferenceRepository groupPreferenceRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    @Transactional
    public void create(String email, GroupPreferenceRequest request) {
        User user = getUser(email);
        // 첫 취향 등록 시만 사용 가능
        if (!user.getContentPreferences().isEmpty()) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        request.getPreferences().forEach(c -> {
            GroupPreference groupPreference = GroupPreference.builder()
                .category(c)
                .user(user)
                .build();
            groupPreferenceRepository.save(groupPreference);
        });
    }

    public List<GroupPreferenceDTO> findAll() {
        final List<GroupPreference> groupPreferences = groupPreferenceRepository.findAll(Sort.by("id"));
        return groupPreferences.stream()
                .map(groupPreference -> mapToDTO(groupPreference, new GroupPreferenceDTO()))
                .toList();
    }

    public GroupPreferenceDTO get(final Long id) {
        return groupPreferenceRepository.findById(id)
                .map(groupPreference -> mapToDTO(groupPreference, new GroupPreferenceDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final GroupPreferenceDTO groupPreferenceDTO) {
        final GroupPreference groupPreference = new GroupPreference();
        mapToEntity(groupPreferenceDTO, groupPreference);
        return groupPreferenceRepository.save(groupPreference).getId();
    }

    public void update(final Long id, final GroupPreferenceDTO groupPreferenceDTO) {
        final GroupPreference groupPreference = groupPreferenceRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(groupPreferenceDTO, groupPreference);
        groupPreferenceRepository.save(groupPreference);
    }

    public void delete(final Long id) {
        groupPreferenceRepository.deleteById(id);
    }

    private GroupPreferenceDTO mapToDTO(final GroupPreference groupPreference,
            final GroupPreferenceDTO groupPreferenceDTO) {
        groupPreferenceDTO.setId(groupPreference.getId());
        groupPreferenceDTO.setCategory(groupPreference.getCategory());
        groupPreferenceDTO.setUser(groupPreference.getUser() == null ? null : groupPreference.getUser().getEmail());
        return groupPreferenceDTO;
    }

    private GroupPreference mapToEntity(final GroupPreferenceDTO groupPreferenceDTO,
            final GroupPreference groupPreference) {
        groupPreference.setCategory(groupPreferenceDTO.getCategory());
        final User user = groupPreferenceDTO.getUser() == null ? null : userRepository.findById(groupPreferenceDTO.getUser())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        groupPreference.setUser(user);
        return groupPreference;
    }

}
