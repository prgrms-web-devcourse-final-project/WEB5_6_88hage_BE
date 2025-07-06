package com.grepp.funfun.app.model.user.entity;

import com.grepp.funfun.app.model.auth.code.Role;
import com.grepp.funfun.app.model.preference.entity.ContentPreference;
import com.grepp.funfun.app.model.preference.entity.GroupPreference;
import com.grepp.funfun.app.model.user.code.Gender;
import com.grepp.funfun.app.model.user.code.UserStatus;
import com.grepp.funfun.infra.entity.BaseEntity;
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
import java.util.HashSet;
import java.util.Set;
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
    private Set<GroupPreference> groupPreferences = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ContentPreference> contentPreferences = new HashSet<>();

}
