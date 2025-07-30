package com.grepp.funfun.app.domain.group.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grepp.funfun.app.domain.group.dto.payload.GroupRequest;
import com.grepp.funfun.app.domain.group.vo.GroupClassification;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.participant.entity.Participant;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"group\"")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // 모임 소개
    private String explain;

    // 모임 조회수
    private Integer viewCount = 0;

    // 모임 한 줄 소개
    private String simpleExplain;

    private String placeName;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime groupDate;

    private Integer maxPeople;

    private Integer nowPeople;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private GroupStatus status;

    private Double latitude;

    private Double longitude;

    //소요 시간
    private Integer during;

    @Enumerated(EnumType.STRING)
    private GroupClassification category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupHashtag> hashtags;

    public void updateViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public void changeStatusAndActivated(GroupStatus status) {
        this.unActivated();
        this.status = status;
    }

    public void minusGroupCount(){
        this.nowPeople--;
    }

    public void changeStatus(GroupStatus status) {
        this.status = status;
    }

    public void approveCount(Integer count){
        this.nowPeople += count;
    }

    @Override
    public String toString() {
        return "id: " + id + "  title: " + title + "  모임 설명: " + explain +
            " 모임 장소는 '" + placeName + '\'' +
            "이고 정확한 주소는 '" + address + '\'' +
            "입니다." +
            " 카테고리는 " + category.getKoreanName() + "입니다.";
    }

    // 수정용 메서드
    public void applyUpdateFrom(GroupRequest request, String newImageUrl) {
        this.title = request.getTitle();
        this.explain = request.getExplain();
        this.simpleExplain = request.getSimpleExplain();
        this.placeName = request.getPlaceName();
        this.groupDate = request.getGroupDate();
        this.address = request.getAddress();
        this.category = request.getCategory();
        this.maxPeople = request.getMaxPeople();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.during = request.getDuring();
        if (newImageUrl != null) {
            this.imageUrl = newImageUrl;
        }
    }
}
