package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Answer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class AnswerDTO {
  private String questionId;
  private String content;
  private String answerId;
  private String userId;
  private String groupId;
  private LocalDateTime createdAt;

  public static AnswerDTO answerToDTO(Answer answer) {
    AnswerDTO answerDTO = new AnswerDTO();
    answerDTO.setAnswerId(answer.getId());
    answerDTO.setQuestionId(answer.getQuestion().getId());
    answerDTO.setContent(answer.getContent());
    answerDTO.setUserId(answer.getUser().getId());
    answerDTO.setGroupId(answer.getGroup().getId());
    answerDTO.setCreatedAt(answer.getCreatedAt());
    return answerDTO;
  }
}
