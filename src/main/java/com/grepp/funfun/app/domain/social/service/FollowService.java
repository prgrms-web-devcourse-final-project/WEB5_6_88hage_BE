package com.grepp.funfun.app.domain.social.service;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import com.grepp.funfun.app.domain.social.dto.FollowDTO;
import com.grepp.funfun.app.domain.social.entity.Follow;
import com.grepp.funfun.app.domain.social.repository.FollowRepository;
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
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findById(email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));
    }

    @Transactional
    public void follow(String followerEmail, String followeeEmail) {
        if (followerEmail.equals(followeeEmail)) {
            // 자기 자신은 팔로우할 수 없습니다.
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }

        boolean exists = followRepository.existsByFollowerEmailAndFolloweeEmail(followerEmail, followeeEmail);
        if (!exists) {
            Follow follow = new Follow();
            follow.setFollower(getUser(followerEmail));
            follow.setFollowee(getUser(followeeEmail));
            followRepository.save(follow);
        } else {
            // 이미 팔로우한 사용자입니다.
            throw new CommonException(ResponseCode.BAD_REQUEST);
        }
    }

    @Transactional
    public void unfollow(String followerEmail, String followeeEmail) {
        boolean exists = followRepository.existsByFollowerEmailAndFolloweeEmail(followerEmail, followeeEmail);
        if (!exists) {
            // 팔로우 관계가 아닙니다.
         throw new CommonException(ResponseCode.BAD_REQUEST);
        }
        followRepository.deleteByFollowerEmailAndFolloweeEmail(followerEmail, followeeEmail);
    }

    public List<FollowsResponse> getFollowers(String email) {
        return followRepository.findFollowersByFolloweeEmail(email);
    }

    public List<FollowsResponse> getFollowings(String email) {
        return followRepository.findFollowingsByFollowerEmail(email);
    }

    public Long countFollowers(String email) {
        return followRepository.countByFolloweeEmail(email);
    }

    public Long countFollowings(String email) {
        return followRepository.countByFollowerEmail(email);
    }

    public boolean isFollowing(String followerEmail, String followeeEmail) {
        return followRepository.existsByFollowerEmailAndFolloweeEmail(followerEmail, followeeEmail);
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
