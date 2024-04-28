package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionDTO {
  private String id;
  private String title;
  private String content;
  private String authorId;
  private String groupId;
  private LocalDateTime createdAt;

  public static QuestionDTO questionToDTO(Question question) {
    return new QuestionDTO(
        question.getId(),
        question.getTitle(),
        question.getContent(),
        question.getAuthor().getId(),
        question.getGroup().getId(),
        question.getCreatedAt()
        );
  }
}
