package com.grepp.funfun.app.domain.user.entity;

import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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
public class UserInfo extends BaseEntity {

    @Id
    private String email;

    private String imageUrl;

    private String introduction;

    @OneToMany(mappedBy = "info",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserHashtag> hashtags = new ArrayList<>();
}
