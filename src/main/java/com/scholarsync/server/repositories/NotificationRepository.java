package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface NotificationRepository extends JpaRepository<Notification, String>{

    Optional<Notification> findByNotificationId(String notificationId);
}
