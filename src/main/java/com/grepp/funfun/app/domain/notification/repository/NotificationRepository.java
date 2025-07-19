package com.grepp.funfun.app.domain.notification.repository;

import com.grepp.funfun.app.domain.notification.entity.Notification;
import com.grepp.funfun.app.domain.notification.vo.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByEmailOrderByIdDesc(String email);

    List<Notification> findTop10ByEmailOrderByIdDesc(String email);

    List<Notification> findAllByEmailAndIsReadFalseOrderByIdDesc(String email);

    int countByEmailAndIsReadFalse(String email);

    boolean existsByEmailAndTypeAndScheduledAtBetween(String email, NotificationType type,
                                                      LocalDateTime start, LocalDateTime end);
    void deleteByEmail(String email);
}
