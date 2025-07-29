package com.grepp.funfun.app.domain.recommend.repository;

import com.grepp.funfun.app.domain.content.document.ContentEmbedding;
import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentEmbeddingRepository extends MongoRepository<ContentEmbedding, String> {
    List<ContentEmbedding> findByStartTimeEpochGreaterThanEqualAndEndTimeEpochLessThanEqual(Long startTime, Long endTime);

    List<ContentEmbedding> findByGunameContainingAndEventType(String guname, String place);

    Optional<GroupEmbedding> findByIdAndTitle(String id, String title);
}