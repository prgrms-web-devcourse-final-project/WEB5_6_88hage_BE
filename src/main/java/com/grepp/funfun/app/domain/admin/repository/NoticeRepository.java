package com.grepp.funfun.app.domain.admin.repository;

import com.grepp.funfun.app.domain.admin.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
