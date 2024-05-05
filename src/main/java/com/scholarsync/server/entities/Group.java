package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "groups")
@Setter
@Getter
public class Group {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "group_id", unique = true)
  String id;

  @Column(name = "title", unique = true)
  String title;

  @Column(name = "description")
  String description;

  @Column(name = "isPrivate")
  boolean isPrivate;

  @ManyToOne
  @JoinColumn(name = "created_by")
  @JsonBackReference
  private User createdBy;

  @ManyToMany(mappedBy = "groups")
  @JsonIgnoreProperties("groups")
  Set<User> users = new HashSet<>();

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
  private Set<GroupInvitation> groupInvitations;

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
  private Set<Question> questions;
}
