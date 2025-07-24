package com.grepp.funfun.app.domain.content.document;

import com.grepp.funfun.app.domain.content.entity.Content;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
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
    private String category;

    private LocalDate startDate;
    private LocalDate endDate;
//    private List<DayOfWeek> availableDays;
//    private LocalTime startTime;
//    private Duration runTime;

    public ContentEmbedding(Content entity, TextSegment segment, Embedding embedding) {
        this.id = entity.getId().toString();
        this.contentTitle = entity.getContentTitle();
        this.address = entity.getAddress();
        this.category = entity.getCategory().getCategory().getKoreanName();
        this.text = segment.text();
        this.embedding = embedding.vector();

        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();

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

    // 임베딩 저장 시 메타데이터도 함께 저장하는 메서드
    public static void saveWithMetadata(Content entity, EmbeddingModel model,
        EmbeddingStore<TextSegment> embeddingStore) {
        TextSegment segment = TextSegment.from(entity.toString());
        Embedding embeddingVector = model.embed(segment).content();

        // 메타데이터 생성
        Metadata metadata = Metadata.from(Map.of(
            "contentTitle", entity.getContentTitle(),
            "address", entity.getAddress(),
            "category", entity.getCategory().getCategory().getKoreanName(),
            "startDate", entity.getStartDate().toString(),
            "endDate", entity.getEndDate().toString()
        ));

        // 메타데이터와 함께 저장
//        embeddingStore.add(embeddingVector, segment, metadata);
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
