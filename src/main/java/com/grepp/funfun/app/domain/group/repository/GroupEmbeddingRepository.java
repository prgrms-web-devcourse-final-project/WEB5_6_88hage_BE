package com.grepp.funfun.app.domain.group.repository;

import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupEmbeddingRepository extends MongoRepository<GroupEmbedding, String> {

}
