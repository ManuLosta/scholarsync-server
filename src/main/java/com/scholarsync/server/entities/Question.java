package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

@Entity
@Getter
@Setter
@Table(
    name = "question",
    indexes = {@Index(name = "title_index", columnList = "title")})
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(unique = true, nullable = false)
  String id;

  @Column(nullable = false, length = 200)
  @Length(min = 10, max = 200)
  String title;

  @Column(nullable = false, length = 1000)
  @Length(min = 10, max = 1000)
  String content;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "author_id")
  @JsonBackReference
  private User author;

  @ManyToOne
  @JoinColumn(name = "group_id")
  @JsonBackReference
  private Group group;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  private Set<QuestionFiles> questionFiles;
}
