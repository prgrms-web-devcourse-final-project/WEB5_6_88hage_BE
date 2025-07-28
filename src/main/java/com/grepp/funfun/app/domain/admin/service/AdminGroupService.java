package com.grepp.funfun.app.domain.admin.service;

import com.grepp.funfun.app.domain.admin.dto.payload.AdminGroupResponse;
import com.grepp.funfun.app.domain.admin.repository.AdminGroupRepository;
import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import com.grepp.funfun.app.domain.user.entity.User;
import com.grepp.funfun.app.domain.user.repository.UserRepository;
import com.grepp.funfun.app.infra.error.exceptions.CommonException;
import com.grepp.funfun.app.infra.response.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminGroupService {

    private final AdminGroupRepository adminGroupRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<Group> getGroupsByStatus(GroupStatus status, Pageable pageable) {
        return adminGroupRepository.findByStatus(status, pageable);
    }

    @Transactional
    public void deleteGroupByAdmin(Long groupId, String adminEmail, String reason) {
        Group group = adminGroupRepository.findById(groupId)
                .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND));

        User admin = userRepository.findByEmail(adminEmail);
        if (admin == null || !admin.getRole().name().equals("ROLE_ADMIN")) {
            throw new CommonException(ResponseCode.UNAUTHORIZED);
        }

        group.changeStatusAndActivated(GroupStatus.DELETE);
        String baseExplain = group.getExplain() != null ? group.getExplain() : "";
        group.setExplain(baseExplain + "\n[관리자 삭제 사유: " + reason + "]");
    }
}
