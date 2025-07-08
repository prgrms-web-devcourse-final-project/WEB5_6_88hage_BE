package com.grepp.funfun.app.model.content.entity;

import com.grepp.funfun.app.model.content.code.ContentClassification;
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
public class ContentCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentClassification category;

}
