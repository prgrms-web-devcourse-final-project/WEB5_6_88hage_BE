package com.grepp.funfun.app.model.social.service;

import com.grepp.funfun.app.model.social.dto.FollowDTO;
import com.grepp.funfun.app.model.social.entity.Follow;
import com.grepp.funfun.app.model.social.repository.FollowRepository;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.app.model.user.repository.UserRepository;
import com.grepp.funfun.infra.error.exceptions.CommonException;
import com.grepp.funfun.infra.response.ResponseCode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(final FollowRepository followRepository,
            final UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public List<FollowDTO> findAll() {
        final List<Follow> follows = followRepository.findAll(Sort.by("id"));
        return follows.stream()
                .map(follow -> mapToDTO(follow, new FollowDTO()))
                .toList();
    }

    public FollowDTO get(final Long id) {
        return followRepository.findById(id)
                .map(follow -> mapToDTO(follow, new FollowDTO()))
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    public Long create(final FollowDTO followDTO) {
        final Follow follow = new Follow();
        mapToEntity(followDTO, follow);
        return followRepository.save(follow).getId();
    }

    public void update(final Long id, final FollowDTO followDTO) {
        final Follow follow = followRepository.findById(id)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        mapToEntity(followDTO, follow);
        followRepository.save(follow);
    }

    public void delete(final Long id) {
        followRepository.deleteById(id);
    }

    private FollowDTO mapToDTO(final Follow follow, final FollowDTO followDTO) {
        followDTO.setId(follow.getId());
        followDTO.setFollower(follow.getFollower() == null ? null : follow.getFollower().getEmail());
        followDTO.setFollowee(follow.getFollowee() == null ? null : follow.getFollowee().getEmail());
        return followDTO;
    }

    private Follow mapToEntity(final FollowDTO followDTO, final Follow follow) {
        final User follower = followDTO.getFollower() == null ? null : userRepository.findById(followDTO.getFollower())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        follow.setFollower(follower);
        final User followee = followDTO.getFollowee() == null ? null : userRepository.findById(followDTO.getFollowee())
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
        follow.setFollowee(followee);
        return follow;
    }

}
