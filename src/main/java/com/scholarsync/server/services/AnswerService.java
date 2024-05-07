package com.scholarsync.server.services;

import com.scholarsync.server.dtos.AnswerDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

  @Autowired AnswerFileRepository answerFileRepository;

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
      String questionId, String content, String userId, String groupId, List<MultipartFile> files) {
    Answer answer;
    try {
      answer = createAnswer(questionId, content, userId, groupId);
    } catch (RuntimeException e) {
      return ResponseEntity.status(errorMap.get(e.getMessage())).body(e.getMessage());
    }

    addFiles(files, answer);

    answerRepository.save(answer);

    return ResponseEntity.ok(AnswerDTO.answerToDTO(answer));
  }

  private Answer createAnswer(String questionId, String content, String userId, String groupId) {
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

    Optional<Group> optionalGroup = groupRepository.findById(groupId);
    if (optionalGroup.isEmpty()) {
      throw new RuntimeException("group/not-found");
    }
    Group group = optionalGroup.get();
    answer.setGroup(group);

    userRepository.save(user);
    groupRepository.save(group);

    return answer;
  }

  private void addFiles(List<MultipartFile> files, Answer answer) {

    if (files != null) {
      if (files.isEmpty()) {
        return;
      }
      Set<AnswerFiles> answerFiles =
          files.stream()
              .map(
                  file -> {
                    AnswerFiles answerFile = new AnswerFiles();
                    try {
                      answerFile.setFile(file.getBytes());
                      answerFile.setFileName(file.getOriginalFilename());
                      answerFile.setFileType(file.getContentType());
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    answerFile.setAnswer(answer);
                    answerFileRepository.save(answerFile);
                    return answerFile;
                  })
              .collect(Collectors.toSet());

      answer.setAnswerFiles(answerFiles);
      return;
    }
  }

  public ResponseEntity<Object> upVoteAnswer(String answerId) {
    Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
    if (optionalAnswer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("answer/not-found");
    }
    Answer answer = optionalAnswer.get();
    answer.setUpvotes(answer.getUpvotes() + 1);
    answerRepository.save(answer);
    User user = answer.getUser();
    user.addCredits(user);
    userRepository.save(user);
    return ResponseEntity.ok("answer/up-voted");
  }

  public ResponseEntity<Object> downVoteAnswer(String answerId) {
    Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
    if (optionalAnswer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("answer/not-found");
    }
    Answer answer = optionalAnswer.get();
    answer.setDownvotes(answer.getDownvotes() + 1);
    answerRepository.save(answer);
    return ResponseEntity.ok("answer/down-voted");
  }

  public ResponseEntity<Object> getImages(String answerId) {
    List<Map<String, String>> images;
    try {
      images = getImagesList(answerId);
    } catch (RuntimeException e) {
      return ResponseEntity.status(errorMap.get(e.getMessage())).body(e.getMessage());
    }
    return ResponseEntity.ok(images);
  }

  public List<Map<String, String>> getImagesList(String answerId) {
    Optional<Answer> answerOptional = answerRepository.findById(answerId);
    if (answerOptional.isEmpty()) {
      throw new RuntimeException("answer/not-found");
    }
    Answer answer = answerOptional.get();

    List<Map<String, String>> images =
        answer.getAnswerFiles().stream()
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

  public ResponseEntity<Object> editAnswer(String answerId, String content) {
    Optional<Answer> optionalAnswer = answerRepository.findById(answerId);
    if (optionalAnswer.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("answer/not-found");
    }
    Answer answer = optionalAnswer.get();
    answer.setContent(content);
    answerRepository.save(answer);
    return ResponseEntity.ok("answer/edited");
  }

  @Transactional
  public ResponseEntity<Object> downloadFile(String id) {
    Optional<AnswerFiles> answerFilesOptional = answerFileRepository.findById(id);
    if (answerFilesOptional.isEmpty()) {
      return ResponseEntity.status(404).body("file/not-found");
    }
    AnswerFiles answerFiles = answerFilesOptional.get();
    byte[] file = answerFiles.getFile();
    String fileName = answerFiles.getFileName();
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=" + fileName);
    headers.add("Content-Type", answerFiles.getFileType());

    return ResponseEntity.ok().headers(headers).body(file);
  }
}
