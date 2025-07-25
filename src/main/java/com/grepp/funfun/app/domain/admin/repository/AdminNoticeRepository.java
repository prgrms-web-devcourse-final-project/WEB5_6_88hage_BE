package com.grepp.funfun.app.domain.admin.repository;

import com.grepp.funfun.app.domain.admin.entity.AdminNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminNoticeRepository extends JpaRepository<AdminNotice, Long> {
    Page<AdminNotice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
