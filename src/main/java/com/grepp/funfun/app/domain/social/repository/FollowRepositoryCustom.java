package com.grepp.funfun.app.domain.social.repository;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepositoryCustom {
    Page<FollowsResponse> findFollowersByFolloweeEmail(String followeeEmail, Pageable pageable);
    Page<FollowsResponse> findFollowingsByFollowerEmail(String followerEmail, Pageable pageable);
}
