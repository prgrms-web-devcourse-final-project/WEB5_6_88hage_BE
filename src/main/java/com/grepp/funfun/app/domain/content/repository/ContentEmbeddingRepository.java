package com.grepp.funfun.app.domain.content.repository;



import com.grepp.funfun.app.domain.content.document.ContentEmbedding;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContentEmbeddingRepository extends MongoRepository<ContentEmbedding, String> {


}