package com.grepp.funfun.app.domain.content.document;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import com.grepp.funfun.app.domain.content.repository.ContentEmbeddingRepository;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContentEmbeddingTest {

    @Autowired
    ContentEmbeddingRepository contentEmbeddingRepository;

    @Autowired
    EmbeddingModel model;

    @Test
    public void save(){
        Content content = new Content();
        content.setId(1L);
        content.setContentTitle("test");
        content.setAddress("서울특별시");

        ContentCategory category = new ContentCategory();
//        category.setCategory(ContentClassification.MOVIE);
//        category.setDuring(2);
        content.setCategory(category);

        contentEmbeddingRepository.save(ContentEmbedding.fromEntity(content, model));
        assertThat(contentEmbeddingRepository.existsById("1")).isTrue();
    }


}