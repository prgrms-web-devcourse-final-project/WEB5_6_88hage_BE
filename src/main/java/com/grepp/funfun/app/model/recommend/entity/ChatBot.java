package com.grepp.funfun.app.model.recommend.entity;

import com.grepp.funfun.app.model.calendar.code.ActivityType;
import com.grepp.funfun.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
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
