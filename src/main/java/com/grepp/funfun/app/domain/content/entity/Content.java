package com.grepp.funfun.app.domain.content.entity;

import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 컨텐츠 아이디

    private String contentTitle; // 컨텐츠명

    private String status; // 컨텐츠 상태

    private String fee; // 이용 금액

    private LocalDate startDate; // 시작일

    private LocalDate endDate; // 종료일

    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    private String reservationUrl; // 예약 링크

    private String guName; // 자치구

    private Integer runTime; // 공연 런타임

    private LocalTime startTime; // 시작 시간

    private LocalTime endTime; // 종료 시간

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentImage> images = new ArrayList<>();

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

}
