package com.grepp.funfun.app.domain.user.service;

import com.grepp.funfun.app.delete.util.ReferencedWarning;
import com.grepp.funfun.app.domain.group.service.GroupService;
import com.grepp.funfun.app.domain.participant.service.ParticipantService;
import com.grepp.funfun.app.domain.s3.service.S3FileService;
import com.grepp.funfun.app.domain.social.service.FollowService;
import com.grepp.funfun.app.domain.user.dto.UserInfoDTO;
import com.grepp.funfun.app.domain.user.dto.payload.ProfileRequest;
import com.grepp.funfun.app.domain.user.dto.payload.UserDetailResponse;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.entity.UserInfo;
import com.grepp.funfun.app.domain.user.repository.UserInfoRepository;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;
    private final S3FileService s3FileService;
    private final FollowService followService;
    private final GroupService groupService;
    private final ParticipantService participantService;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    @Transactional
    public void update(String email, ProfileRequest request) {
        User user = getUser(email);

        UserInfo userInfo = user.getInfo();

        // 1. 소개글 변경
        userInfo.setIntroduction(request.getIntroduction());

        // 2. 이미지 처리
        if (request.isImageChanged()) {
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                // 새 이미지 업로드
                String newImageUrl = s3FileService.upload(request.getImage(), "user");
                userInfo.setImageUrl(newImageUrl);
            } else {
                userInfo.setImageUrl(null);
            }
        }
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(String email) {
        User user = getUser(email);

        UserInfo userInfo = user.getInfo();

        long followerCount = followService.countFollowers(email);
        long followingCount = followService.countFollowings(email);

        long groupLeadCount = groupService.countLeadGroupByEmail(email);
        long groupJoinCount = participantService.countJoinGroupByEmail(email);

        return UserDetailResponse.builder()
            .email(userInfo.getEmail())
            .nickname(user.getNickname())
            .introduction(userInfo.getIntroduction())
            .imageUrl(userInfo.getImageUrl())
            .followerCount(followerCount)
            .followingCount(followingCount)
            .groupLeadCount(groupLeadCount)
            .groupJoinCount(groupJoinCount)
            .build();
    }

    @Transactional(readOnly = true)
    public List<UserInfoDTO> findAll() {
        final List<UserInfo> userInfoes = userInfoRepository.findAll(Sort.by("email"));
        return userInfoes.stream()
                .map(userInfo -> mapToDTO(userInfo, new UserInfoDTO()))
                .toList();
    }

    public UserInfoDTO get(final String email) {
        return userInfoRepository.findById(email)
                .map(userInfo -> mapToDTO(userInfo, new UserInfoDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public String create(final UserInfoDTO userInfoDTO) {
        final UserInfo userInfo = new UserInfo();
        mapToEntity(userInfoDTO, userInfo);
        userInfo.setEmail(userInfoDTO.getEmail());
        return userInfoRepository.save(userInfo).getEmail();
    }

    public void update(final String email, final UserInfoDTO userInfoDTO) {
        final UserInfo userInfo = userInfoRepository.findById(email)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(userInfoDTO, userInfo);
        userInfoRepository.save(userInfo);
    }

    public void delete(final String email) {
        userInfoRepository.deleteById(email);
    }

    private UserInfoDTO mapToDTO(final UserInfo userInfo, final UserInfoDTO userInfoDTO) {
        userInfoDTO.setEmail(userInfo.getEmail());
        userInfoDTO.setImageUrl(userInfo.getImageUrl());
        userInfoDTO.setIntroduction(userInfo.getIntroduction());
//        userInfoDTO.setHashtags(
//            userInfo.getHashtags().stream()
//                .map(UserHashtag::getTag)
//                .toList()
//        );
        return userInfoDTO;
    }

    private UserInfo mapToEntity(final UserInfoDTO userInfoDTO, final UserInfo userInfo) {
        userInfo.setImageUrl(userInfoDTO.getImageUrl());
        userInfo.setIntroduction(userInfoDTO.getIntroduction());
        return userInfo;
    }

    public boolean emailExists(final String email) {
        return userInfoRepository.existsByEmailIgnoreCase(email);
    }

    public ReferencedWarning getReferencedWarning(final String email) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final UserInfo userInfo = userInfoRepository.findById(email)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        final User infoUser = userRepository.findFirstByInfo(userInfo);
        if (infoUser != null) {
            referencedWarning.setKey("userInfo.user.info.referenced");
            referencedWarning.addParam(infoUser.getEmail());
            return referencedWarning;
        }
        return null;
    }

}
