package com.grepp.funfun.app.domain.social.repository;

import com.grepp.funfun.app.domain.social.dto.payload.FollowsResponse;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepositoryCustom {
    List<FollowsResponse> findFollowersByFolloweeEmail(String followeeEmail);
    List<FollowsResponse> findFollowingsByFollowerEmail(String followerEmail);
}
