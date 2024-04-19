package com.scholarsync.server.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(unique = true)
  private String id;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime created;

  @Column(name = "expires_at")
  private LocalDateTime expires;

  @OneToOne
  @JoinColumn(name = "userId")
  private User user;

  public Session() {
    this.expires = LocalDateTime.now().plusDays(1);
  }
}
