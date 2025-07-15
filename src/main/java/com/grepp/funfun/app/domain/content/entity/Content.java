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
    private Long id; // 컨텐츠 아이디

    @Column(nullable = false)
    private String contentTitle; // 컨텐츠명

    private String age; // 나이

    private String fee; // 이용 금액

    private LocalDate startDate; // 시작일

    private LocalDate endDate; // 종료일

    private String address;

    private Double latitude;

    private Double longitude;

    private String guname; // 자치구

    private String time;

    private String runTime; // 공연 런타임

    private String startTime; // 시작 시간

    private String poster;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @BatchSize(size = 10) // BatchSize 설정
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentUrl> urls = new ArrayList<>();  // 사이트 링크

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentImage> images = new ArrayList<>(); // 이미지 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ContentCategory category;

    @Column(nullable = false)
    private Integer bookmarkCount;

    @Override
    public String toString() {
        return "Content{" +
            "id=" + id +
            ", 이벤트 타입은 '" + eventType.name() + '\'' +
            "이고 행사 제목은 '" + contentTitle + '\'' +
            "이고 나이제한은 '" + age + '\'' +
            "이고 요금은 '" + fee + '\'' +
            "이고 행사 시작날짜는 " + startDate +
            "이고 행사 종료 일자는 " + endDate +
            "이며 행사 장소의 위도값은 " + latitude +
            "이고 경도값은 " + longitude +
            "이야 행사는 '" + guname + '\'' +
            "에서 진행하고 시간은 '" + time + '\'' +
            "에 진행하며 소요시간은 '" + runTime + '\'' +
            "이고 정확한 시작시간은 '" + startTime + '\'' +
            "이고 세부 내용은'" + description + '\'' +
            "이고 카테고리는 " + category.getCategory().getKoreanName() +
            '}';
    }
    public void increaseBookmark() {
        this.bookmarkCount++;
    }

    public void decreaseBookmark() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }

}
