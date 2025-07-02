package com.grepp.funfun.app.model.user.entity;

import com.grepp.funfun.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class UserInfo extends BaseEntity {

    @Id
    private String email;

    private String imageUrl;

    private String introduction;

}
