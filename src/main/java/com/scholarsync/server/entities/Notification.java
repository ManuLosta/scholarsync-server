package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String notificationId;

    @Enumerated(EnumType.STRING)
    private NotificationType notifactionType;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    public String getNotificationId() {
        return notificationId;
    }

    private void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public NotificationType getNotifactionType() {
        return notifactionType;
    }

    void setNotifactionType(NotificationType notifactionType) {
        this.notifactionType = notifactionType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
