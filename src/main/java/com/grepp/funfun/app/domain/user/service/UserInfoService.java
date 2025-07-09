package com.grepp.funfun.app.domain.user.service;

import com.grepp.funfun.app.domain.user.dto.UserInfoDTO;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.entity.UserInfo;
import com.grepp.funfun.app.domain.user.repository.UserInfoRepository;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import com.grepp.funfun.app.delete.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserRepository userRepository;

    public UserInfoService(final UserInfoRepository userInfoRepository,
            final UserRepository userRepository) {
        this.userInfoRepository = userInfoRepository;
        this.userRepository = userRepository;
    }

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
