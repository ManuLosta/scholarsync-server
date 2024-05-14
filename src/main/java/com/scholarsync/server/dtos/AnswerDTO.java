package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Answer;
import com.scholarsync.server.entities.Rating;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class AnswerDTO {
  private String questionId;
  private String content;
  private String answerId;
  private ProfileDTO author;
  private String groupId;
  private String groupTitle;
  private List<FileDTO> files;
  private LocalDateTime createdAt;
  private List<RatingDTO> ratings;

  public static AnswerDTO answerToDTO(Answer answer) {
    AnswerDTO answerDTO = new AnswerDTO();
    answerDTO.setAnswerId(answer.getId());
    answerDTO.setQuestionId(answer.getQuestion().getId());
    answerDTO.setContent(answer.getContent());
    answerDTO.setAuthor(ProfileDTO.userToProfileDTO(answer.getUser()));
    answerDTO.setGroupId(answer.getGroup().getId());
    answerDTO.setCreatedAt(answer.getCreatedAt());
    answerDTO.setFiles(answer.getAnswerFiles().stream().map(FileDTO::fileToDTO).toList());
    List<RatingDTO> ratings = new ArrayList<>();
    Set<Rating> ratingsSet = answer.getRatings();

    if (ratingsSet != null) {
      for (Rating rating : ratingsSet) {
        ratings.add(RatingDTO.ratingToDTO(rating));
      }
    }

    answerDTO.setRatings(ratings);

    return answerDTO;
  }
}
