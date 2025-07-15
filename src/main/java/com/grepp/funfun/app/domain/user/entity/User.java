package com.grepp.funfun.app.domain.user.entity;

import com.grepp.funfun.app.domain.auth.vo.Role;
import com.grepp.funfun.app.domain.preference.entity.ContentPreference;
import com.grepp.funfun.app.domain.preference.entity.GroupPreference;
import com.grepp.funfun.app.domain.user.dto.payload.UserInfoRequest;
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
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "\"user\"")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    private String email;

    private String password;

    private String nickname;

    private String birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String address;

    private Double latitude;

    private Double longitude;

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

    public void updateFromRequest(UserInfoRequest request) {
        this.address = request.getAddress();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.birthDate = request.getBirthDate();
        this.gender = request.getGender();
        this.isMarketingAgreed = request.getIsMarketingAgreed();

        // OAuth2 사용자가 추가 정보 기입 완료하면 ROLE_USER 로 전환
        if (this.role == Role.ROLE_GUEST) {
            this.role = Role.ROLE_USER;
        }
    }

    public boolean isAdmin() {
        return this.role == Role.ROLE_ADMIN;
    }

    public String getGroupPreferencesToString(){
        if(groupPreferences == null){
            return "나는 특별한 취향이 없습니다";
        }

        String preferences = groupPreferences.stream()
            .map(preference -> preference.getCategory().getKoreanName())
            .collect(Collectors.joining(", "));

        return "나는" + preferences + "활동을 선호해.";
    }

    public String getContentPreferencesToString(){
        if(groupPreferences == null){
            return "나는 특별한 취향이 없습니다";
        }

        String preferences = contentPreferences.stream()
                                             .map(preference -> preference.getCategory().getKoreanName())
                                             .collect(Collectors.joining(", "));

        return "나는" + preferences + "활동을 선호해.";
    }
}
