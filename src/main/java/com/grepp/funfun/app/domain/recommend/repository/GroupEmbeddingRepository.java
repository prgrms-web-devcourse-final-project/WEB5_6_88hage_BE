package com.grepp.funfun.app.domain.recommend.repository;

import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupEmbeddingRepository extends MongoRepository<GroupEmbedding, String> {

    List<GroupEmbedding> findByStartTimeEpochGreaterThanEqualAndEndTimeEpochLessThanEqual(Long startTime, Long endTime);

    Optional<GroupEmbedding> findByIdAndTitle(String id, String title);
}
