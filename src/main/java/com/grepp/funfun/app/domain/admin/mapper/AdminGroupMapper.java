package com.grepp.funfun.app.domain.admin.mapper;

import com.grepp.funfun.app.domain.admin.dto.payload.AdminGroupResponse;
import com.grepp.funfun.app.domain.group.entity.Group;
import org.springframework.stereotype.Component;

@Component
public class AdminGroupMapper {

    public AdminGroupResponse toDto(Group group) {
        return AdminGroupResponse.builder()
                .id(group.getId())
                .title(group.getTitle())
                .leaderEmail(group.getLeader().getEmail())
                .leaderNickname(group.getLeader().getNickname())
                .status(group.getStatus())
                .participantCount(group.getParticipants().size())
                .groupDate(group.getGroupDate())
                .deletedReason(extractReason(group.getExplain()))
                .build();
    }
    private String extractReason(String explain) {
        if (explain == null) return null;

        String prefix = "관리자 삭제 사유:";
        int index = explain.indexOf(prefix);
        if (index == -1) return null;

        return explain.substring(index + prefix.length()).trim();
    }
}