package com.grepp.funfun.app.infra.init;


import com.grepp.funfun.app.domain.content.document.ContentEmbedding;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import com.grepp.funfun.app.domain.recommend.repository.ContentEmbeddingRepository;
import com.grepp.funfun.app.domain.recommend.repository.GroupEmbeddingRepository;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class DataInitializeService {

    private final ContentEmbeddingRepository contentEmbeddingRepository;
    private final GroupEmbeddingRepository groupEmbeddingRepository;
    private final EmbeddingModel embeddingModel;
    private final ContentRepository contentRepository;
    private final GroupRepository groupRepository;

    public DataInitializeService(
        ContentEmbeddingRepository contentEmbeddingRepository,
        GroupEmbeddingRepository groupEmbeddingRepository,
        EmbeddingModel embeddingModel,
        ContentRepository contentRepository,
        GroupRepository groupRepository,
        @Qualifier("contentEmbeddingStore") EmbeddingStore<TextSegment> mongoDbContentEmbeddingStore,
        @Qualifier("groupEmbeddingStore") EmbeddingStore<TextSegment> mongoDbGroupEmbeddingStore) {
        this.contentEmbeddingRepository = contentEmbeddingRepository;
        this.groupEmbeddingRepository = groupEmbeddingRepository;
        this.embeddingModel = embeddingModel;
        this.contentRepository = contentRepository;
        this.groupRepository = groupRepository;
   }

    @Transactional
    public void initializeVector() {

        if (contentEmbeddingRepository.count() > 0) {
            log.info("=====================================================================");
            log.info("컨텐츠는 이미 초기화 되었습니다!!");
            log.info("컨텐츠 개수: {}", contentEmbeddingRepository.count());

        } else {
            List<Content> contents = contentRepository.findAllWithCategory();
            List<ContentEmbedding> embeddings = contents.stream()
                                                        .map(entity -> ContentEmbedding.fromEntity(
                                                            entity, embeddingModel))
                                                        .toList();

            contentEmbeddingRepository.saveAll(embeddings);
        }

        if (groupEmbeddingRepository.count() > 0) {
            log.info("=====================================================================");
            log.info("모임은 이미 초기화 되었습니다!!");
            log.info("모임 개수: {}", groupEmbeddingRepository.count());

        } else {
            List<Group> groups = groupRepository.findAll();
            List<GroupEmbedding> embeddings = groups.stream()
                                                    .map(entity -> GroupEmbedding.fromEntity(
                                                        entity, embeddingModel
                                                    ))
                                                    .toList();

            groupEmbeddingRepository.saveAll(embeddings);
        }
    }
}

