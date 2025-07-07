package com.grepp.funfun.app.model.social.repository;

import com.grepp.funfun.app.model.social.entity.Follow;
import com.grepp.funfun.app.model.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FollowRepository extends JpaRepository<Follow, Long> {

    Follow findFirstByFollower(User user);

    Follow findFirstByFollowee(User user);

    boolean existsByFollowerEmailAndFolloweeEmail(String followerEmail, String followeeEmail);
}
