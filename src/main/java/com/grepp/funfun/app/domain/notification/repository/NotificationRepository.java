package com.grepp.funfun.app.domain.notification.repository;

import com.grepp.funfun.app.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
