package com.scholarsync.server.services;

import com.scholarsync.server.dtos.AnswerDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AnswerService {

  @Autowired QuestionRepository questionRepository;

  @Autowired UserRepository userRepository;

  @Autowired GroupRepository groupRepository;

  @Autowired AnswerRepository answerRepository;

  @Autowired FileRepository fileRepository;

  @Autowired RatingRepository ratingRepository;

  @Autowired CreditService creditService;

  @Autowired LevelService levelService;

  Map<String, HttpStatusCode> errorMap =
      Map.of(
          "question/not-found",
          HttpStatus.NOT_FOUND,
          "user/not-found",
          HttpStatus.NOT_FOUND,
          "group/not-found",
          HttpStatus.NOT_FOUND,
          "answer/not-found",
          HttpStatus.NOT_FOUND);

  @Transactional
  public ResponseEntity<Object> answerQuestion(
      String questionId, String content, String userId, List<MultipartFile> files) {
    Answer answer;
    try {
      answer = createAnswer(questionId, content, userId);
    } catch (RuntimeException e) {
      return ResponseEntity.status(errorMap.get(e.getMessage())).body(e.getMessage());
    }

    addFiles(files, answer);

    answerRepository.save(answer);

    levelService.giveXp(answer.getUser(), 10);

    return ResponseEntity.ok(AnswerDTO.answerToDTO(answer));
  }

  private Answer createAnswer(String questionId, String content, String userId) {
    Answer answer = new Answer();
    answer.setContent(content);

    Optional<Question> optionalQuestion = questionRepository.findById(questionId);
    if (optionalQuestion.isEmpty()) {
      throw new RuntimeException("question/not-found");
    }
    Question question = optionalQuestion.get();
    answer.setQuestion(question);

    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isEmpty()) {
      throw new RuntimeException("user/not-found");
    }
    User user = optionalUser.get();
    answer.setUser(user);

    Group group = question.getGroup();
    answer.setGroup(group);

    userRepository.save(user);
    groupRepository.save(group);

    return answer;
  }


  public ResponseEntity<Object> rateAnswer(String answerId, String userId, double rating) {
    if (rating < 0 || rating > 5) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("rating/invalid");
    }
    Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
    if (optionalAnswer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("answer/not-found");
    }
    Answer answer = optionalAnswer.get();
    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");
    }

    Optional<Rating> rating1 = ratingRepository.findByAnswerAndUserId(answer, optionalUser.get());
    if (rating1.isPresent()) {
      rating1.get().setRating(rating);
      ratingRepository.save(rating1.get());
    } else {
      User user = optionalUser.get();
      Rating newRating = new Rating();
      newRating.setRating(rating);
      newRating.setAnswer(answer);
      newRating.setUserId(user);
      if (answer.getRatings() == null) answer.setRatings(new HashSet<>());
      answer.getRatings().add(newRating);
      user.getRatings().add(newRating);
      ratingRepository.save(newRating);

      creditService.awardAnswer(answer, user, newRating);
    }

    int ratingCount = answer.getRatings().size();
    double ratingAverage = getAnswerRatingAverage(answer);
    Map<String, Object> answerMap = new HashMap<>();
    answerMap.put("ratingCount", ratingCount);
    answerMap.put("ratingAverage", ratingAverage);
    return ResponseEntity.ok(answerMap);
  }

  @Transactional
  public ResponseEntity<Object> getImages(String answerId) {
    List<Map<String, String>> images;
    try {
      images = getImagesList(answerId);
    } catch (RuntimeException e) {
      return ResponseEntity.status(errorMap.get(e.getMessage())).body(e.getMessage());
    }
    return ResponseEntity.ok(images);
  }



  @SuppressWarnings("DuplicatedCode")
  @Transactional
  public ResponseEntity<Object> editAnswer(
      String userId, String answerId, String content, List<MultipartFile> files) {
    Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
    if (optionalAnswer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("answer/not-found");
    }
    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");
    }
    if (!optionalAnswer.get().getUser().getId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user/not-authorized");
    }
    Answer answer = optionalAnswer.get();
    answer.setContent(content);
    if (files != null) {
      fileRepository.deleteAll(answer.getFiles());
      addFiles(files, answer);
    }
    answerRepository.save(answer);

    return ResponseEntity.ok(AnswerDTO.answerToDTO(answer));
  }

  @SuppressWarnings("DuplicatedCode")
  @Transactional
  public ResponseEntity<Object> deleteAnswer(String userId, String answerId) {
    Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
    if (optionalAnswer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("answer/not-found");
    }
    Optional<User> optionalUser = userRepository.findById(userId);
    if (optionalUser.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user/not-found");
    }
    if (!optionalAnswer.get().getUser().getId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user/not-authorized");
    }
    Answer answer = optionalAnswer.get();
    fileRepository.deleteAll(answer.getFiles());
    answerRepository.delete(answer);
    return ResponseEntity.ok("answer/deleted");
  }


  @Transactional
  public ResponseEntity<Object> getAnswersByQuestion(String questionId) {
    Optional<Question> questionOptional = questionRepository.findById(questionId);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();
    List<Answer> answers = new ArrayList<>(question.getAnswers());
    List<Answer> sortedAnswers =
            answers.stream()
                    .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                    .toList();
    List<AnswerDTO> answerDTOS = sortedAnswers.stream().map(AnswerDTO::answerToDTO).toList();
    return ResponseEntity.ok(answerDTOS);
  }


  // ------helper methods------------------------------------------

  private void addFiles(List<MultipartFile> files, Answer answer) {

    if (files != null) {
      if (files.isEmpty()) {
        return;
      }
      Set<Files> answerFiles =
              files.stream()
                      .map(
                              file -> {
                                Files answerFile = new Files();
                                try {
                                  answerFile.setFile(file.getBytes());
                                  answerFile.setFileName(file.getOriginalFilename());
                                  answerFile.setFileType(file.getContentType());
                                } catch (IOException e) {
                                  e.printStackTrace();
                                }
                                fileRepository.save(answerFile);
                                return answerFile;
                              })
                      .collect(Collectors.toSet());

      answer.setFiles(answerFiles);
      answerRepository.save(answer);
      return;
    }
  }

  public List<Map<String, String>> getImagesList(String answerId) {
    Optional<Answer> answerOptional = answerRepository.findById(answerId);
    if (answerOptional.isEmpty()) {
      throw new RuntimeException("answer/not-found");
    }
    Answer answer = answerOptional.get();

    List<Map<String, String>> images =
            answer.getFiles().stream()
                    .filter(answerFiles -> answerFiles.getFileType().contains("image"))
                    .map(
                            answerFiles -> {
                              Map<String, String> imageMap = new HashMap<>();
                              imageMap.put("fileType", answerFiles.getFileType());
                              imageMap.put(
                                      "base64Encoding", Base64.getEncoder().encodeToString(answerFiles.getFile()));
                              imageMap.put("name", answer.getId());
                              return imageMap;
                            })
                    .collect(Collectors.toList());

    return images;
  }

  public double getAnswerRatingAverage(Answer answer) {
    return answer.getRatings().stream().mapToDouble(Rating::getRating).average().orElse(0);
  }
}
