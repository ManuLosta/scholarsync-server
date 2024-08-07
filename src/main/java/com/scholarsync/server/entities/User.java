package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.scholarsync.server.types.levelType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
@Setter
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", unique = true)
  private String id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(unique = true)
  private String username;

  @Column private String password;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "credits")
  private int credits;

  @Column(name = "xp")
  private int xp;

  @Column(name = "level", nullable = false)
  @Enumerated(EnumType.STRING)
  private levelType level;

  @Column(name = "google_refresh_token")
  private String googleRefreshToken;



  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_picture_id", referencedColumnName = "id")
  private Files profilePicture;

  @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  @JsonIgnoreProperties({"createdBy", "users"})
  private Set<Group> owner;

  @ManyToMany
  @JoinTable(
      name = "user_group",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id"))
  @JsonIgnoreProperties("users")
  private Set<Group> groups;

  @ManyToMany
  @JoinTable(
      name = "friend_with",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "friend_id"))
  private Set<User> friends;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  private Set<Notification> receivedNotifications;

  @OneToMany(mappedBy = "from", cascade = CascadeType.ALL)
  private Set<FriendRequest> sentFriendRequests;

  @OneToMany(mappedBy = "to", cascade = CascadeType.ALL)
  private Set<FriendRequest> receivedFriendRequests;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<GroupInvitation> receivedGroupInvitations;

  @OneToMany(mappedBy = "invitedBy", cascade = CascadeType.ALL)
  private Set<GroupInvitation> sentGroupInvitations;

  @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
  private Set<Question> questions;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Answer> answers;

  @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
  private Set<Rating> ratings;


  @ManyToOne
  @JoinColumn(name = "chat_id")
  @JsonBackReference
  private Chat chat;


  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Event> events;

  public User() {
    this.credits = 100;
    this.xp = 0;
    this.level = levelType.Newbie;
  }
}
