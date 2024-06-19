package com.scholarsync.server.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GoogleUser {
  @Id
  @Column(name = "user_id")
  private String id;

  @Column(name = "google_id")
  private String googleId;

  @Column(name = "google_email")
  private String email;

  @Column(name = "refresh_token")
  private String refreshToken;

  @OneToOne(mappedBy = "googleUser")
  @MapsId
  private User user;
}
