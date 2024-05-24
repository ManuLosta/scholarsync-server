package com.scholarsync.server.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatFile {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String fileName;

  private String fileType;

  @Lob
  private byte[] data;

  @ManyToOne
  @JoinColumn(name = "chat_id")
  @JsonBackReference
  private Chat chat;
}
