package com.grepp.funfun.app.domain.content.entity;

import com.grepp.funfun.app.domain.content.vo.EventType;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "content")
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

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentUrl> urls = new ArrayList<>();  // 사이트 링크

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentImage> images = new ArrayList<>(); // 이미지 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ContentCategory category;

    @Column(nullable = false)
    private Integer bookmarkCount = 0;

    public void increaseBookmark() {
        this.bookmarkCount++;
    }

    public void decreaseBookmark() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }

}
