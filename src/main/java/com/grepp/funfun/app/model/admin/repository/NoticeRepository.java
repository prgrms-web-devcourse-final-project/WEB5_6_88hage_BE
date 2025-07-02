package com.grepp.funfun.app.model.admin.repository;

import com.grepp.funfun.app.model.admin.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
