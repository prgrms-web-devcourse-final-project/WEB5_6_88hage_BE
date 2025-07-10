package com.grepp.funfun.app.infra.init;


import com.grepp.funfun.app.domain.content.document.ContentEmbedding;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.repository.ContentEmbeddingRepository;
import com.grepp.funfun.app.domain.content.repository.ContentRepository;
import dev.langchain4j.model.embedding.EmbeddingModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DataInitializeService {

    private final ContentEmbeddingRepository contentEmbeddingRepository;
    private final EmbeddingModel embeddingModel;
    private final ContentRepository contentRepository;

    @Transactional
    public void initializeVector() {

        if (contentEmbeddingRepository.count() > 0) {
            //System.out.println(contentEmbeddingRepository.findAll());
            System.out.println("=====================================================================");
            System.out.println("이미 초기화 되었습니다!!");
            return;
        }



        List<Content> contents = contentRepository.findAll();

        List<ContentEmbedding> embeddings = contents.stream()
                                                    .map(entity -> ContentEmbedding.fromEntity(
                                                         entity, embeddingModel))
                                                    .toList();

        contentEmbeddingRepository.saveAll(embeddings);

    }

}
