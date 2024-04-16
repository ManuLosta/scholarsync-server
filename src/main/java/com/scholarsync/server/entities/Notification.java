package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.scholarsync.server.types.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Notification {
  @Id
  @Setter
  @Getter
  @GeneratedValue(strategy = GenerationType.UUID)
  private String notificationId;

  @Enumerated(EnumType.STRING)
  @Setter
  @Getter
  private NotificationType notificationType;

  @ManyToOne
  @JsonBackReference
  @Setter
  @Getter
  @JoinColumn(name = "owner_id")
  private User owner;

  @Setter
  @Getter
  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

}
