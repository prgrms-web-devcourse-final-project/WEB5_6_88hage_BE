package com.grepp.funfun.app.domain.social.service;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import com.grepp.funfun.app.domain.social.entity.Follow;
import com.grepp.funfun.app.domain.social.repository.FollowRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
            throw new CommonException(ResponseCode.BAD_REQUEST, "자기 자신은 팔로우할 수 없습니다.");
        }

        boolean exists = followRepository.existsByFollowerEmailAndFolloweeEmail(followerEmail, followeeEmail);
        if (exists) {
            // 이미 팔로우한 사용자입니다.
            throw new CommonException(ResponseCode.BAD_REQUEST, "이미 팔로우한 사용자입니다.");
        }

        followRepository.save(
            Follow.builder()
                .follower(getUser(followerEmail))
                .followee(getUser(followeeEmail))
                .build()
        );
    }

    @Transactional
    public void unfollow(String followerEmail, String followeeEmail) {
        boolean exists = followRepository.existsByFollowerEmailAndFolloweeEmail(followerEmail, followeeEmail);
        if (!exists) {
            // 팔로우 관계가 아닙니다.
         throw new CommonException(ResponseCode.BAD_REQUEST, "팔로우 관계가 아닙니다.");
        }
        followRepository.deleteByFollowerEmailAndFolloweeEmail(followerEmail, followeeEmail);
    }

    public Page<FollowsResponse> getFollowers(String email, Pageable pageable) {
        return followRepository.findFollowersByFolloweeEmail(email, pageable);
    }

    public Page<FollowsResponse> getFollowings(String email, Pageable pageable) {
        return followRepository.findFollowingsByFollowerEmail(email, pageable);
    }

    public long countFollowers(String email) {
        return followRepository.countByFolloweeEmail(email);
    }

    public long countFollowings(String email) {
        return followRepository.countByFollowerEmail(email);
    }

    public boolean isFollowing(String myEmail, String targetEmail) {
        return followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail);
    }

    public boolean isFollower(String myEmail, String targetEmail) {
        return followRepository.existsByFollowerEmailAndFolloweeEmail(targetEmail, myEmail);
    }

}
