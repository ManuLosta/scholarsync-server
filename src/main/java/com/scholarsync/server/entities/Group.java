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
@Getter
@Setter
@Table(name = "groups")
public class Group {

  @Getter
  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "group_id", unique = true)
  String id;

  @Getter
  @Setter
  @Column(name = "title", unique = true)
  String title;

  @Getter
  @Setter
  @Column(name = "description")
  String description;

  @Setter
  @Getter
  @Column(name = "isPrivate")
  boolean isPrivate;

  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "created_by")
  @JsonBackReference
  private User createdBy;

  @Getter
  @Setter
  @ManyToMany(mappedBy = "groups")
  @JsonIgnoreProperties("groups")
  Set<User> users = new HashSet<>();


  @Getter
  @Setter
  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
  private Set<GroupInvitation> groupInvitations;

}
