package com.grepp.funfun.app.model.group.entity;

import com.grepp.funfun.app.model.group.code.GroupClassification;
import com.grepp.funfun.app.model.group.code.GroupStatus;
import com.grepp.funfun.app.model.participant.entity.Participant;
import com.grepp.funfun.app.model.user.entity.User;
import com.grepp.funfun.infra.entity.BaseEntity;
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
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "\"group\"")
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String explain;

    private String placeName;

    private String address;

    private LocalDateTime groupDate;

    private Integer maxPeople;

    private Integer nowPeople;

    @Enumerated(EnumType.STRING)
    private GroupStatus status;

    private String imageUrl;

    private Double latitude;

    private Double longitude;

    private Integer during;

    @Enumerated(EnumType.STRING)
    private GroupClassification category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @OneToMany(mappedBy = "group")
    private Set<Participant> participants = new HashSet<>();

    @OneToMany(mappedBy = "group")
    private Set<GroupHashTag> hashTags = new HashSet<>();

}
