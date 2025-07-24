package com.grepp.funfun.app.domain.admin.dto.payload;

import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminGroupResponse {
    private Long id;
    private String title;
    private String leaderEmail;
    private String leaderNickname;
    private GroupStatus status;
    private int participantCount;
    private LocalDateTime groupDate;
    private String deletedReason;
    private LocalDateTime createdAt;
}