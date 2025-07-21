package com.grepp.funfun.app.domain.admin.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import com.grepp.funfun.app.domain.group.vo.GroupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminGroupRepositoryCustom {
    Page<Group> findByStatus(GroupStatus status, Pageable pageable);
}
