package com.scholarsync.server.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "group_id"}))
public class Chat {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String name;

  @ManyToOne
  @JoinColumn(name = "group_id")
  @JsonBackReference
  private Group group;


  @OneToMany
  private Set<Files> files;

  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
  private Set<User> users;


  @CreationTimestamp
  private LocalDateTime createdAt;

}
