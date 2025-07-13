package com.grepp.funfun.app.domain.content.entity;

import com.grepp.funfun.app.domain.content.vo.EventType;
import com.grepp.funfun.app.domain.content.dto.ContentDTO;
import com.grepp.funfun.app.domain.content.dto.payload.RecommendContentResponse;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;


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

    @Override
    public String toString() {
        return "Content{" +
            "id=" + id +
            ", 행사 제목은'" + contentTitle + '\'' +
            "이고 행사 시작날짜는" + startDate +
            "이고 행사 종료 일자는" + endDate +
            "이며 행사는 '" + address + '\'' +
            "에서 진행하고 정확한 위도는" + latitude +
            "이고 경도는" + longitude +
            "이야 행사는'" + guName + '\'' +
            "에서 진행하고 행사 카테고리는" + category + "이야" +
            '}';
    }

    public RecommendContentResponse toResponseWithImages() {
        return RecommendContentResponse.builder()
                                       .id(this.id)
                                       .contentTitle(this.contentTitle)
                                       .fee(this.fee)
                                       .startDate(this.startDate)
                                       .endDate(this.endDate)
                                       .address(this.address)
                                       .reservationUrl(this.reservationUrl)
                                       .guName(this.guName)
                                       .runTime(this.runTime)
                                       .startTime(this.startTime)
                                       .endTime(this.endTime)
                                       .category(this.category.getCategory().getKoreanName())
                                       .bookmarkCount(this.bookmarkCount)
                                       .images(this.images.stream().map(ContentImage::getImageUrl).collect(Collectors.toList()))
                                       .build();
    }



}
