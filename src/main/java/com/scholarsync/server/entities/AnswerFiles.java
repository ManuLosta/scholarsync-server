package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AnswerFiles {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "answer_id")
  @JsonBackReference
  private Answer answer;

  @Lob
  @Column(name = "file")
  private byte[] file;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "file_type")
  private String fileType;
}
