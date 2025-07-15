package com.grepp.funfun.app.domain.content.document;

import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.content.entity.ContentCategory;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.persistence.Id;
import java.util.Arrays;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection  = "contents")
public class ContentEmbedding {

    @Id
    private String id;
    private String text;
    private float[] embedding;

    // 필요 없는 필드 삭제 예정 or 시간에 대한 필드 추가할 지 말지
    private String contentTitle;
    private String address;
    private ContentCategory category;

    public ContentEmbedding(Content entity, TextSegment segment, Embedding embedding) {
        this.id = entity.getId().toString();
        this.contentTitle = entity.getContentTitle();
        this.address = entity.getAddress();
        this.category = entity.getCategory();
        this.text = segment.text();
        this.embedding = embedding.vector();
    }

    // entity를 받아서 임베딩
    public static ContentEmbedding fromEntity(Content entity, EmbeddingModel model){
        TextSegment segment = TextSegment.from(entity.toString());
        Embedding embedding = model.embed(segment).content();
        return new ContentEmbedding(entity, segment, embedding);
    }

    // 데이터 업데이트 되었을 때 임베딩 ( 코드 수정 필요 )
    public void embed(EmbeddingModel model){
        TextSegment segment = TextSegment.from(this.toString());
        this.text = segment.text();
        Embedding embedding = model.embed(segment).content();
        this.embedding = embedding.vector();
    }

    @Override
    public String toString() {
        return "ContentsEmbedding{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", embedding=" + Arrays.toString(embedding) +
            ", contentTitle='" + contentTitle + '\'' +
            ", address='" + address + '\'' +
            ", category=" + category +
            '}';
    }
}
