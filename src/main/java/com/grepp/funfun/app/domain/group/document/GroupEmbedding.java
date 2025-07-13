package com.grepp.funfun.app.domain.group.document;

import com.grepp.funfun.app.domain.group.entity.Group;
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
@Document(collection = "groups")
public class GroupEmbedding {

    @Id
    private String id;
    private String text;
    private float[] embedding;

    private String groupTitle;
    private String address;

    public GroupEmbedding(Group entity, TextSegment segment, Embedding embedding) {
        this.id = entity.getId().toString();
        this.text = segment.text();
        this.embedding = embedding.vector();
        this.groupTitle = entity.getTitle();
        this.address = entity.getAddress();
    }

    public static GroupEmbedding fromEntity(Group entity, EmbeddingModel model){
        TextSegment segment = TextSegment.from(entity.toString());
        Embedding embedding = model.embed(segment).content();
        return new GroupEmbedding(entity, segment, embedding);
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
        return "GroupEmbedding{" +
            "id='" + id + '\'' +
            ", text='" + text + '\'' +
            ", embedding=" + Arrays.toString(embedding) +
            ", groupTitle='" + groupTitle + '\'' +
            ", address='" + address + '\'' +
            '}';
    }
}
