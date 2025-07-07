package com.grepp.funfun.app.model.social.repository;

import com.grepp.funfun.app.controller.api.social.payload.FollowsResponse;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepositoryCustom {
    List<FollowsResponse> findFollowersByFolloweeEmail(String followeeEmail);
}
