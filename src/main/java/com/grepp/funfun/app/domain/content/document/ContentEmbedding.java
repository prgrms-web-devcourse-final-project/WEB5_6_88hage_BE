package com.grepp.funfun.app.domain.content.document;

import com.grepp.funfun.app.domain.content.entity.Content;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import jakarta.persistence.Id;
import java.time.ZoneOffset;
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

    private String title;
    private String guname;
    private String category;
    private Long startTimeEpoch;
    private Long endTimeEpoch;
    private String eventType;

    public ContentEmbedding(Content entity, TextSegment segment, Embedding embedding) {
        this.id = entity.getId().toString();
        this.text = segment.text();
        this.embedding = embedding.vector();
        this.title = entity.getContentTitle();
        this.category = entity.getCategory().getCategory().getKoreanName();
        this.guname = entity.getGuname();

        Long startEpochMillis = 1000L;
        Long endEpochMillis = 1000L;
        if (entity.getStartDate() != null) {
            startEpochMillis = entity.getStartDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        }
        if (entity.getEndDate() != null) {
            endEpochMillis = entity.getEndDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        }

        this.startTimeEpoch = startEpochMillis;
        this.endTimeEpoch = endEpochMillis;
        this.eventType = entity.getEventType().toString();
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
            '}';
    }
}
