package com.grepp.funfun.app.domain.user.entity;

import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import com.grepp.funfun.app.domain.user.vo.Gender;
import com.grepp.funfun.app.domain.user.vo.UserStatus;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "\"user\"")
public class User extends BaseEntity {

    @Id
    private String email;

    private String password;

    private String nickname;

    private String birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private LocalDate dueDate;

    private Integer suspendDuration;

    private String dueReason;

    private Boolean isVerified;

    private Boolean isMarketingAgreed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "info_id", unique = true)
    private UserInfo info;

    @OneToMany(mappedBy = "user")
    private List<GroupPreference> groupPreferences = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ContentPreference> contentPreferences = new ArrayList<>();

}
