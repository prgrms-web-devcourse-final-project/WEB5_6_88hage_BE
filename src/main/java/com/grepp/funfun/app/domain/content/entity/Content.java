package com.grepp.funfun.app.domain.content.entity;


import com.grepp.funfun.app.domain.content.vo.EventType;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String externalId;

    @Column(nullable = false, length = 500)
    private String contentTitle;

    private String age;

    @Column(length = 500)
    private String fee;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(length = 500)
    private String address;

    private Double latitude;

    private Double longitude;

    private String area;

    private String guname;

    @Column(length = 500)
    private String time;

    private String runTime;

    @Column(length = 500)
    private String startTime;

    private String poster;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentUrl> urls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentImage> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ContentCategory category;

    private Integer bookmarkCount;

    @Override
    public String toString() {

        if(description != null && startDate == null) {  // place 데이터
            return "id: " + id +  // place data
                " 이벤트 타입은 " + eventType.name() +
                " 이고 title: " + contentTitle +
                "  장소는 " + area + guname +
                " 이고 세부 내용은 " + description +
                " 이고 카테고리는 " + category.getCategory().getKoreanName();
        }
        return "id: " + id +  // event data
            " 이벤트 타입: " + eventType.name() +
            " 이고 title: " + contentTitle +
            "  나이제한은 " + age +
            " 입니다. 행사 주소: " + area + " " + guname +
            " 이고 카테고리는 " + category.getCategory().getKoreanName();
    }
    public void increaseBookmark() {
        if (this.bookmarkCount == null) {
            this.bookmarkCount = 0;
        }
        this.bookmarkCount++;
    }

    public void decreaseBookmark() {
        if (this.bookmarkCount == null) {
            this.bookmarkCount = 0;
        }
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }

}
