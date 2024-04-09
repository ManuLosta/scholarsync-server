package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Notification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {

  Optional<Notification> findByNotificationId(String notificationId);
}
