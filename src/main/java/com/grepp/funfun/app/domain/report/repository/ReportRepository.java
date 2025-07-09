package com.grepp.funfun.app.domain.report.repository;

import com.grepp.funfun.app.domain.report.entity.Report;
import com.grepp.funfun.app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findFirstByReportingUser(User user);

    Report findFirstByReportedUser(User user);

}
