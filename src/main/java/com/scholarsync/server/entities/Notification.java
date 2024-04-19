package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String notificationId;

  @Enumerated(EnumType.STRING)
  private NotificationType notificationType;

  @ManyToOne
  @JsonBackReference
  @JoinColumn(name = "owner_id")
  private User owner;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;
}
