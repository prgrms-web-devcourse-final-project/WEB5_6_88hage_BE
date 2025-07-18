package com.grepp.funfun.app.infra.config;

import com.mongodb.client.MongoClient;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.mongodb.IndexMapping;
import dev.langchain4j.store.embedding.mongodb.MongoDbEmbeddingStore;
import java.util.HashSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecommendationConfig {


    @Bean(name = "chatModel")
    public ChatLanguageModel chatModel() {
        return GoogleAiGeminiChatModel.builder()
            .apiKey("AIzaSyCoJ8iF8VWMM084BiTUFhZtcYZFdYZNdKI")
            .modelName("gemini-2.0-flash-lite")
            .logRequestsAndResponses(true)
            .maxOutputTokens(1000)
            .build();
    }

    // 임베딩 모델 정의
    // 문장 -> 벡터로
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    // MongoDB 벡터 DB로 사용하기 위한 설정
    @Bean
    public MongoDbEmbeddingStore embeddingStore(EmbeddingModel embeddingModel,
        MongoClient mongoClient) {

        Boolean createIndex = true;
        IndexMapping indexMapping = IndexMapping.builder()
            .dimension(embeddingModel.dimension())
            .metadataFieldNames(new HashSet<>())
            .build();

        // 벡터 기반 RAG/추천 시스템이 동작할 수 있게 builder 패턴으로 생성
        return MongoDbEmbeddingStore.builder()
            .databaseName("llm")
            .collectionName("restaurants")
            .createIndex(createIndex)
            .indexName("vector_index")
            .indexMapping(indexMapping)
            .fromClient(mongoClient)
            .build();
    }

    // RAG 방식 검색, 추천 시스템, 유사 문장 검색 활용하기 위한 컴포넌트
    @Bean
    EmbeddingStoreContentRetriever embeddingStoreContentRetriever(
        EmbeddingStore<TextSegment> embeddingStore,
        EmbeddingModel embeddingModel
    ){
        return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .maxResults(100)
            .minScore(0.7)
            .build();
    }

}
