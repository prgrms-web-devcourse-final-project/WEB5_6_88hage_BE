package com.grepp.funfun.app.domain.social.service;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import com.grepp.funfun.app.domain.social.entity.Follow;
import com.grepp.funfun.app.domain.social.repository.FollowRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {
    @InjectMocks
    private FollowService followService;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    private String myEmail;

    @BeforeEach
    void setUp() {
        myEmail = "test@test.test";
    }

    @Test
    void follow_OK() {
        // given
        String targetEmail = "target@target.target";

        // when
        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(new User()));
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(false);
        followService.follow(myEmail, targetEmail);

        // then
        verify(followRepository).save(any(Follow.class));
    }

    @Test
    void follow_self_follow_EX() {
        // given
        String targetEmail = myEmail;

        // when, then
        assertThrows(CommonException.class, () -> followService.follow(myEmail, targetEmail));
    }

    @Test
    void follow_duplicate_follow_EX() {
        // given
        String targetEmail = "target@target.target";

        // when, then
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(true);
        assertThrows(CommonException.class, () -> followService.follow(myEmail, targetEmail));
    }

    @Test
    void unFollow_OK() {
        // given
        String targetEmail = "target@target.target";

        // when
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(true);
        followService.unfollow(myEmail, targetEmail);

        // then
        verify(followRepository).deleteByFollowerEmailAndFolloweeEmail(myEmail, targetEmail);
    }

    @Test
    void unFollow_NOT_FOLLOW_EX() {
        // given
        String targetEmail = "target@target.target";

        // when, then
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(false);
        assertThrows(CommonException.class, () -> followService.unfollow(myEmail, targetEmail));
    }

    @Test
    void countFollowers_OK() {
        // given

        // when
        when(followRepository.countByFolloweeEmail(myEmail)).thenReturn(5L);
        long count = followService.countFollowers(myEmail);

        // then
        assertEquals(5L, count);
    }

    @Test
    void countFollowings_OK() {
        // given

        // when
        when(followRepository.countByFollowerEmail(myEmail)).thenReturn(5L);
        long count = followService.countFollowings(myEmail);

        // then
        assertEquals(5L, count);
    }

    @Test
    void isFollowing_OK() {
        // given
        String targetEmail = "target@target.target";

        // when
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(true);
        boolean result = followService.isFollowing(myEmail, targetEmail);

        // then
        assertTrue(result);
    }

    @Test
    void isFollower_OK() {
        // given
        String targetEmail = "target@target.target";

        // when
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(targetEmail, myEmail)).thenReturn(true);
        boolean result = followService.isFollower(myEmail, targetEmail);

        // then
        assertTrue(result);
    }

    @Test
    void getFollowers() {
        // given
        String targetEmail = "target@target.target";
        Pageable pageable = PageRequest.of(0, 10);
        List<FollowsResponse> mockList = List.of(
            FollowsResponse.builder()
                .email(targetEmail)
                .build()
        );
        Page<FollowsResponse> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        // when
        when(followRepository.findFollowersByFolloweeEmail(myEmail, pageable)).thenReturn(mockPage);
        Page<FollowsResponse> result = followService.getFollowers(myEmail, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(targetEmail, result.getContent().getFirst().getEmail());
    }

    @Test
    void getFollowings() {
        // given
        String targetEmail = "target@target.target";
        Pageable pageable = PageRequest.of(0, 10);
        List<FollowsResponse> mockList = List.of(
            FollowsResponse.builder()
                .email(targetEmail)
                .build()
        );
        Page<FollowsResponse> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        // when
        when(followRepository.findFollowingsByFollowerEmail(myEmail, pageable)).thenReturn(mockPage);
        Page<FollowsResponse> result = followService.getFollowings(myEmail, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(targetEmail, result.getContent().getFirst().getEmail());
    }
}