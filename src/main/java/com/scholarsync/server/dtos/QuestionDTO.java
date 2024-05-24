package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Question;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionDTO {
  private String id;
  private String title;
  private String content;
  private ProfileDTO author;
  private String groupId;
  private String groupTitle;
  private List<FileDTO> files;
  private LocalDateTime createdAt;

  public static QuestionDTO questionToDTO(Question question) {
    return new QuestionDTO(
        question.getId(),
        question.getTitle(),
        question.getContent(),
        ProfileDTO.userToProfileDTO(question.getAuthor()),
        question.getGroup().getId(),
        question.getGroup().getTitle(),
        question.getFiles() != null
            ? question.getFiles().stream()
                .map(FileDTO::fileToDTO)
                .collect(Collectors.toList())
            : new ArrayList<>(),
        question.getCreatedAt());
  }
}
