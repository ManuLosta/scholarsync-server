package com.scholarsync.server.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
public class Session {

  @Id
  @Setter
  @Getter
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(unique = true)
  private String id;

  @CreationTimestamp
  @Setter
  @Getter
  @Column(name = "created_at")
  private LocalDateTime created;

  @Setter
  @Getter
  @Column(name = "expires_at")
  private LocalDateTime expires;


  @Setter
  @Getter
  @OneToOne
  @JoinColumn(name = "userId")
  private User user;

  public Session() {
    this.expires = LocalDateTime.now().plusDays(1);
  }


}
