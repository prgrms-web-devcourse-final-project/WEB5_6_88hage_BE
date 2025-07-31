package com.grepp.funfun.app.domain.user.service;

import com.grepp.funfun.app.domain.gcs.service.GCSFileService;
import com.grepp.funfun.app.domain.preference.dto.payload.PreferenceResponse;
import com.grepp.funfun.app.domain.preference.service.PreferenceService;
import com.grepp.funfun.app.domain.social.service.FollowService;
import com.grepp.funfun.app.domain.user.dto.payload.ProfileRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserDetailResponse;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.entity.UserInfo;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInfoService {

    private final UserRepository userRepository;
    private final GCSFileService gcsFileService;
    private final FollowService followService;
    private final PreferenceService preferenceService;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    @Transactional
    public void update(String email, ProfileRequest request) {
        User user = getUser(email);

        UserInfo userInfo = user.getInfo();

        // 1. 소개글 변경
        userInfo.updateIntroduction(request.getIntroduction());

        // 2. 이미지 처리
        if (request.isImageChanged()) {
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                // 새 이미지 업로드
                String newImageUrl = gcsFileService.upload(request.getImage(), "user");
                userInfo.updateImage(newImageUrl);
            } else {
                userInfo.removeImage();
            }
        }
    }

    public UserDetailResponse getUserDetail(String email) {
        User user = getUser(email);

        UserInfo userInfo = user.getInfo();

        long followerCount = followService.countFollowers(email);
        long followingCount = followService.countFollowings(email);
        PreferenceResponse preferenceResponse = preferenceService.get(email);

        return UserDetailResponse.builder()
            .email(userInfo.getEmail())
            .nickname(user.getNickname())
            .introduction(userInfo.getIntroduction())
            .imageUrl(userInfo.getImageUrl())
            .contentPreferences(preferenceResponse.getContentPreferences())
            .groupPreferences(preferenceResponse.getGroupPreferences())
            .followerCount(followerCount)
            .followingCount(followingCount)
            .build();
    }

}
