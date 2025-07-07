package com.grepp.funfun.app.model.content.entity;

import com.grepp.funfun.infra.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

}
