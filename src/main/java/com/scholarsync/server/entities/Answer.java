package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Entity
public class Answer {
  public Answer() {}

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, length = 5000)
  @Length(min = 10, max = 5000)
  private String content;

  @ManyToOne
  @JoinColumn(name = "question_id")
  @JsonBackReference
  private Question question;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  @ManyToOne
  @JoinColumn(name = "group_id")
  @JsonBackReference
  private Group group;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
  private Set<AnswerFiles> answerFiles;

  @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
  private Set<Rating> ratings;
}
