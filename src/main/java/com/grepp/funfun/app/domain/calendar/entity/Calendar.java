package com.grepp.funfun.app.domain.calendar.entity;

import com.grepp.funfun.app.domain.calendar.vo.ActivityType;
import com.grepp.funfun.app.domain.content.entity.Content;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.infra.entity.BaseEntity;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private LocalDateTime selectedDate;

    @Enumerated(EnumType.STRING)
    private ActivityType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    public void updateSelectedDateForContent(LocalDateTime selectedDate) {
        if (this.type == ActivityType.GROUP) {
            throw new CommonException(ResponseCode.BAD_REQUEST, "모임 일정은 직접 수정할 수 없습니다.");
        }

        this.selectedDate = selectedDate;
    }

}
