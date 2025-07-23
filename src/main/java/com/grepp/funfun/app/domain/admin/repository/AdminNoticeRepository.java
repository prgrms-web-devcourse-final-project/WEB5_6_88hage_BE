package com.grepp.funfun.app.domain.admin.repository;

import com.grepp.funfun.app.domain.admin.entity.AdminNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminNoticeRepository extends JpaRepository<AdminNotice, Long> {
    List<AdminNotice> findAllByOrderByCreatedAtDesc();
}
