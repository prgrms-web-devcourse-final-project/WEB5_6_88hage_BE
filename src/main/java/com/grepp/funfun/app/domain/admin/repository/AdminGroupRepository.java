package com.grepp.funfun.app.domain.admin.repository;

import com.grepp.funfun.app.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminGroupRepository extends JpaRepository<Group, Long>, AdminGroupRepositoryCustom {
}