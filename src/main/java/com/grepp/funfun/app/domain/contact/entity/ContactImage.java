package com.grepp.funfun.app.domain.contact.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactImage {
    @Id
    @GeneratedValue
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Contact contact;
}