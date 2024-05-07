package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Answer;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerDTO {
  private String questionId;
  private String content;
  private String userId;
  private String groupId;

  public static AnswerDTO answerToDTO(Answer answer) {
    AnswerDTO answerDTO = new AnswerDTO();
    answerDTO.setQuestionId(answer.getQuestion().getId());
    answerDTO.setContent(answer.getContent());
    answerDTO.setUserId(answer.getUser().getId());
    answerDTO.setGroupId(answer.getGroup().getId());
    return answerDTO;
  }
}