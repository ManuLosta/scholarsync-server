package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users")
public class User {

  @Id
  @Setter
  @Getter
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", unique = true)
  private String id;

  @Column(nullable = false, unique = true)
  @Setter
  @Getter
  private String email;

  @Column(unique = true)
  @Setter
  @Getter
  private String username;


  @Getter
  @Setter
  @Column private String password;

  @Setter
  @Getter
  @Column(name = "first_name")
  private String firstName;

  @Setter
  @Getter
  @Column(name = "last_name")
  private String lastName;

  @Column(name = "birth_date")
  @Setter
  @Getter
  private LocalDate birthDate;

  @CreationTimestamp
  @Setter
  @Getter
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "credits")
  @Setter
  @Getter
  private int credits;

  @Setter
  @Getter
  @Column(name = "xp")
  private int xp;

  @Setter
  @Getter
  @Column(name = "level_id")
  private long levelId = 0;

  @Setter
  @Getter
  @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  @JsonIgnoreProperties({"createdBy", "users"})
  private Set<Group> owner;

  @Setter
  @Getter
  @ManyToMany
  @JoinTable(
      name = "user_group",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "group_id"))
  @JsonIgnoreProperties("users")
  private Set<Group> groups;

  @Setter
  @Getter
  @ManyToMany
  @JoinTable(
      name = "friend_with",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "friend_id"))
  private Set<User> friends;

  @Setter
  @Getter
  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
  private Set<Notification> receivedNotifications;

  @Setter
  @Getter
  @OneToMany(mappedBy = "from", cascade = CascadeType.ALL)
  private Set<FriendRequest> sentFriendRequests;

  @Setter
  @Getter
  @OneToMany(mappedBy = "to", cascade = CascadeType.ALL)
  private Set<FriendRequest> receivedFriendRequests;

  @Setter
  @Getter
  @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
  private Set<GroupInvitation> groupInvitations;

  public User() {}
}
