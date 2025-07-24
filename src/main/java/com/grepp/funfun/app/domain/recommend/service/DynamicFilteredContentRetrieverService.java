package com.grepp.funfun.app.domain.recommend.service;


import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.filter.comparison.IsGreaterThanOrEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsLessThanOrEqualTo;
import dev.langchain4j.store.embedding.filter.logical.And;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import dev.langchain4j.store.embedding.filter.Filter;

@Service
public class DynamicFilteredContentRetrieverService {

    private final EmbeddingModel embeddingModel;
    private final MongoDbEmbeddingStore embeddingStore;

    public DynamicFilteredContentRetrieverService(EmbeddingModel embeddingModel,
        @Qualifier("contentEmbeddingStore") MongoDbEmbeddingStore embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

//     시간 필터가 적용된 동적 retriever 생성
    public EmbeddingStoreContentRetriever createFilteredRetriever(LocalDateTime userStart,
        LocalDateTime userEnd) {
//        // MongoDB 날짜 필터 생성
//        Bson dateFilter = Filters.and(
//            Filters.lte("startDate", userStart.toLocalDate().toString()),
//            Filters.gte("endDate", userEnd.toLocalDate().toString())
//        );
        // LangChain4j MetadataFilter 생성
        Filter startDateFilter = new IsLessThanOrEqualTo("startDate", userEnd.toLocalDate().toString());
        Filter endDateFilter = new IsGreaterThanOrEqualTo("endDate", userStart.toLocalDate().toString());
        Filter dateFilter = new And(startDateFilter, endDateFilter);

        return EmbeddingStoreContentRetriever.builder()
                                             .embeddingModel(embeddingModel)
                                             .embeddingStore(embeddingStore)
                                             .maxResults(100)
                                             .minScore(0.7)
                                             .filter(dateFilter) // 동적 필터 적용
                                             .build();
    }

    // 기본 retriever (필터 없음)
    public EmbeddingStoreContentRetriever createDefaultRetriever() {
        return EmbeddingStoreContentRetriever.builder()
                                             .embeddingModel(embeddingModel)
                                             .embeddingStore(embeddingStore)
                                             .maxResults(100)
                                             .minScore(0.7)
                                             .build();
    }
}
