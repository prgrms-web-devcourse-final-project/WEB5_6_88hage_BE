package com.grepp.funfun.app.domain.social.service;

import com.grepp.funfun.app.domain.social.entity.Follow;
import com.grepp.funfun.app.domain.social.repository.FollowRepository;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void follow_정상() {
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
    void follow_self_follow_예외() {
        // given
        String targetEmail = myEmail;

        // when, then
        assertThrows(CommonException.class, () -> followService.follow(myEmail, targetEmail));
    }

    @Test
    void follow_중복_팔로우_예외() {
        // given
        String targetEmail = "target@target.target";

        // when, then
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(true);
        assertThrows(CommonException.class, () -> followService.follow(myEmail, targetEmail));
    }

    @Test
    void unFollow_정상() {
        // given
        String targetEmail = "target@target.target";

        // when
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(true);
        followService.unfollow(myEmail, targetEmail);

        // then
        verify(followRepository).deleteByFollowerEmailAndFolloweeEmail(myEmail, targetEmail);
    }

    @Test
    void unFollow_NOT_FOLLOW_예외() {
        // given
        String targetEmail = "target@target.target";

        // when, then
        when(followRepository.existsByFollowerEmailAndFolloweeEmail(myEmail, targetEmail)).thenReturn(false);
        assertThrows(CommonException.class, () -> followService.unfollow(myEmail, targetEmail));
    }
}