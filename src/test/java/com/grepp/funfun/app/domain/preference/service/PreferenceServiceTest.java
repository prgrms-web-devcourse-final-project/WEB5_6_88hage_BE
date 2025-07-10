package com.grepp.funfun.app.domain.preference.service;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceRequest;
import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceResponse;
import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import com.grepp.funfun.app.domain.preference.repository.ContentPreferenceRepository;
import com.grepp.funfun.app.domain.preference.repository.GroupPreferenceRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {

    @InjectMocks
    private PreferenceService preferenceService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContentPreferenceRepository contentPreferenceRepository;

    @Mock
    private GroupPreferenceRepository groupPreferenceRepository;

    private String email;

    @BeforeEach
    void setUp(){
        email = "test@test.test";
    }

    @Test
    void create_정상() {
        // given
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .contentPreferences(Set.of(ContentClassification.DANCE, ContentClassification.CLASSIC))
            .groupPreferences(Set.of(GroupClassification.ART, GroupClassification.CULTURE))
            .build();

        // when
        when(userRepository.findById(email)).thenReturn(Optional.of(new User()));
        preferenceService.create(email, preferenceRequest);

        // then
        verify(contentPreferenceRepository, times(2)).save(any(ContentPreference.class));
        verify(groupPreferenceRepository, times(2)).save(any(GroupPreference.class));
    }

    @Test
    void create_재등록_예외() {
        // given
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .contentPreferences(Set.of(ContentClassification.DANCE, ContentClassification.CLASSIC))
            .groupPreferences(Set.of(GroupClassification.ART, GroupClassification.CULTURE))
            .build();

        // when
        User user = User.builder()
            .contentPreferences(List.of())
            .groupPreferences(List.of(new GroupPreference()))
            .build();
        when(userRepository.findById(email)).thenReturn(Optional.of(user));

        // then
        assertThrows(CommonException.class, () -> preferenceService.create(email, preferenceRequest));
    }

    @Test
    void update_정상() {
        // given
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .contentPreferences(Set.of(ContentClassification.DANCE, ContentClassification.CLASSIC))
            .groupPreferences(Set.of(GroupClassification.ART, GroupClassification.CULTURE))
            .build();

        // when
        when(userRepository.findById(email)).thenReturn(Optional.of(new User()));
        preferenceService.update(email, preferenceRequest);

        // then
        verify(contentPreferenceRepository).deleteAllByUserEmail(email);
        verify(groupPreferenceRepository).deleteAllByUserEmail(email);

        verify(contentPreferenceRepository, times(2)).save(any(ContentPreference.class));
        verify(groupPreferenceRepository, times(2)).save(any(GroupPreference.class));
    }

    @Test
    void get_정상() {
        // given
        List<ContentPreference> contentPreferences = List.of(
            ContentPreference.builder()
                .id(1L)
                .category(ContentClassification.DANCE)
                .build()
        );
        List<GroupPreference> groupPreferences = List.of(
            GroupPreference.builder()
                .id(1L)
                .category(GroupClassification.ART)
                .build()
        );

        // when
        when(contentPreferenceRepository.findByUserEmail(email)).thenReturn(contentPreferences);
        when(groupPreferenceRepository.findByUserEmail(email)).thenReturn(groupPreferences);
        PreferenceResponse preferenceResponse = preferenceService.get(email);

        // then
        assertEquals(ContentClassification.DANCE, preferenceResponse.getContentPreferences().stream().findFirst().get());
        assertEquals(GroupClassification.ART, preferenceResponse.getGroupPreferences().stream().findFirst().get());
    }
}