package com.grepp.funfun.app.infra.init;


import com.grepp.funfun.app.domain.content.document.ContentEmbedding;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentEmbeddingRepository;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import com.grepp.funfun.app.domain.group.document.GroupEmbedding;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.repository.GroupEmbeddingRepository;
import com.grepp.funfun.app.domain.group.repository.GroupRepository;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DataInitializeService {

    private final ContentEmbeddingRepository contentEmbeddingRepository;
    private final GroupEmbeddingRepository groupEmbeddingRepository;
    private final EmbeddingModel embeddingModel;
    private final ContentRepository contentRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public void initializeVector() {

        if (contentEmbeddingRepository.count() > 0) {
            log.info("=====================================================================");
            log.info("컨텐츠는 이미 초기화 되었습니다!!");

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
