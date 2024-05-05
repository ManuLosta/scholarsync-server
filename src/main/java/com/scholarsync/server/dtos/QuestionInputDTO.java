package com.scholarsync.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionInputDTO {
  private String title;
  private String content;
  private String authorId;
  private String groupId;
}
