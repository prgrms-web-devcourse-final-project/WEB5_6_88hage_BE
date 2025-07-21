package com.grepp.funfun.app.domain.user.entity;

import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo extends BaseEntity {

    @Id
    private String email;

    private String imageUrl;

    private String introduction;

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void removeImage() {
        this.imageUrl = null;
    }

}
