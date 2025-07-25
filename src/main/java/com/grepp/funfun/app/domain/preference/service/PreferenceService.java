package com.grepp.funfun.app.domain.preference.service;

import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceRequest;
import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceResponse;
import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import com.grepp.funfun.app.domain.preference.repository.ContentPreferenceRepository;
import com.grepp.funfun.app.domain.preference.repository.GroupPreferenceRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.HashSet;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreferenceService {

    private final ContentPreferenceRepository contentPreferenceRepository;
    private final GroupPreferenceRepository groupPreferenceRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    @Transactional
    public void create(String email, PreferenceRequest request) {
        User user = getUser(email);
        // 첫 취향 등록 시만 사용 가능
        if (!user.getContentPreferences().isEmpty() || !user.getGroupPreferences().isEmpty()) {
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        preferencesSave(request, user);
    }

    @Transactional
    public void update(String email, PreferenceRequest request) {
        User user = getUser(email);
        // 이전 컨텐츠 취향 모두 삭제
        contentPreferenceRepository.deleteAllByUserEmail(email);
        groupPreferenceRepository.deleteAllByUserEmail(email);

        preferencesSave(request, user);
    }

    private void preferencesSave(PreferenceRequest request, User user) {
        request.getContentPreferences().forEach(c -> {
            ContentPreference contentPreference = ContentPreference.builder()
                .category(c)
                .user(user)
                .build();
            contentPreferenceRepository.save(contentPreference);
        });

        request.getGroupPreferences().forEach(c -> {
            GroupPreference groupPreference = GroupPreference.builder()
                .category(c)
                .user(user)
                .build();
            groupPreferenceRepository.save(groupPreference);
        });
    }

    public PreferenceResponse get(String email) {
        return PreferenceResponse.builder()
            .contentPreferences(contentPreferenceRepository.findByUserEmail(email)
                .stream()
                .map(ContentPreference::getCategory).collect(Collectors.toCollection(
                    HashSet::new)))
            .groupPreferences(groupPreferenceRepository.findByUserEmail(email)
                .stream()
                .map(GroupPreference::getCategory).collect(Collectors.toCollection(
                    HashSet::new)))
            .build();
    }

    public boolean hasPreferences(String email) {
        return !contentPreferenceRepository.findByUserEmail(email).isEmpty()
            && !groupPreferenceRepository.findByUserEmail(email).isEmpty();
    }
}
