package com.grepp.funfun.app.domain.user.service;

import com.grepp.funfun.app.domain.content.vo.ContentClassification;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceResponse;
import com.grepp.funfun.app.domain.preference.service.PreferenceService;
import com.grepp.funfun.app.domain.s3.service.S3FileService;
import com.grepp.funfun.app.domain.social.service.FollowService;
import com.grepp.funfun.app.domain.user.dto.payload.ProfileRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserDetailResponse;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.entity.UserInfo;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {
    @InjectMocks
    private UserInfoService userInfoService;

    @Mock
    private S3FileService s3FileService;

    @Mock
    private FollowService followService;

    @Mock
    private PreferenceService preferenceService;

    @Mock
    private UserRepository userRepository;

    private String email;
    private User user;
    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        email = "test@test.test";
        userInfo = UserInfo.builder()
            .email(email)
            .introduction("한 줄 소개")
            .imageUrl("image-url")
            .build();
        user = User.builder()
            .email(email)
            .nickname("닉네임")
            .info(userInfo)
            .build();

        when(userRepository.findById(email)).thenReturn(Optional.of(user));
    }

    @Test
    void update_NOT_IMAGE_CHANGED_OK() {
        // given
        ProfileRequest request = ProfileRequest.builder()
            .introduction("소개 변경")
            .imageChanged(false)
            .build();

        // when
        userInfoService.update(email, request);

        // then
        assertEquals("소개 변경", userInfo.getIntroduction());
    }

    @Test
    void update_IMAGE_CHANGED_OK() {
        // given
        MockMultipartFile image = new MockMultipartFile("image", "filename.jpg", "image/jpeg", "fake-image".getBytes());
        ProfileRequest request = ProfileRequest.builder()
            .introduction("소개 변경")
            .image(image)
            .imageChanged(true)
            .build();

        // when
        when(s3FileService.upload(image, "user")).thenReturn("url2");
        userInfoService.update(email, request);

        // then
        assertEquals("소개 변경", userInfo.getIntroduction());
        assertEquals("url2", userInfo.getImageUrl());
    }

    @Test
    void update_IMAGE_REMOVE_OK() {
        // given
        ProfileRequest request = ProfileRequest.builder()
            .introduction("소개 변경")
            .imageChanged(true)
            .build();

        // when
        userInfoService.update(email, request);

        // then
        assertEquals("소개 변경", userInfo.getIntroduction());
        assertNull(userInfo.getImageUrl());
    }

    @Test
    void getUserDetail_WithValidUser_ShouldReturnCorrectUserDetailResponse() {
        // given
        PreferenceResponse preference = PreferenceResponse.builder()
            .groupPreferences(Set.of(GroupClassification.ART, GroupClassification.CULTURE))
            .contentPreferences(Set.of(ContentClassification.DANCE))
            .build();

        // when
        when(followService.countFollowers(email)).thenReturn(5L);
        when(followService.countFollowings(email)).thenReturn(10L);
        when(preferenceService.get(email)).thenReturn(preference);
        UserDetailResponse result = userInfoService.getUserDetail(email);

        // then
        assertEquals(email, result.getEmail());
        assertEquals("닉네임", result.getNickname());
        assertEquals("한 줄 소개", result.getIntroduction());
        assertEquals("image-url", result.getImageUrl());
        assertEquals(5L, result.getFollowerCount());
        assertEquals(10L, result.getFollowingCount());
        assertEquals(Set.of(GroupClassification.ART, GroupClassification.CULTURE), result.getGroupPreferences());
        assertEquals(Set.of(ContentClassification.DANCE), result.getContentPreferences());
    }
}