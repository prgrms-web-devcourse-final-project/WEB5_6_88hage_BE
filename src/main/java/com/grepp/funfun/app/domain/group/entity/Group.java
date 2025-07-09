package com.grepp.funfun.app.domain.group.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grepp.funfun.app.domain.chat.entity.ChatRoom;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"group\"")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String explain;

    private String placeName;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime groupDate;

    private Integer maxPeople;

    private Integer nowPeople;

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

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    @OneToOne(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "group",cascade = CascadeType.ALL)
    private List<GroupHashtag> hashtags;

}
