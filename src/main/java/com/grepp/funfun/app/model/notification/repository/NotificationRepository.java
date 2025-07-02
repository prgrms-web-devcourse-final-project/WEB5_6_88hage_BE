package com.grepp.funfun.app.model.notification.repository;

import com.grepp.funfun.app.model.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
