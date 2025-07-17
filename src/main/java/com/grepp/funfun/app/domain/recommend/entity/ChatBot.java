package com.grepp.funfun.app.domain.recommend.entity;

import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatBot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String groupSummary;

    private String contentSummary;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

}
