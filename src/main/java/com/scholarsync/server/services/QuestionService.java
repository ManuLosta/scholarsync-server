package com.scholarsync.server.services;


import com.scholarsync.server.dtos.FileDTO;
import com.scholarsync.server.dtos.QuestionDTO;
import com.scholarsync.server.dtos.QuestionInputDTO;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import jakarta.transaction.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuestionService {

  @Autowired QuestionRepository questionRepository;
  @Autowired UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private FileRepository fileRepository;
  @Autowired private AnswerRepository answerRepository;

  @Transactional
  public ResponseEntity<Object> getQuestion(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();
    QuestionDTO response = QuestionDTO.questionToDTO(question);

    return ResponseEntity.ok(response);
  }

  @Transactional
  public ResponseEntity<Object> getFiles(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();
    List<Files> files = new ArrayList<>(question.getFiles());
    FileDTO[] fileDTOs = new FileDTO[files.size()];
    for (Files file : files) {
      FileDTO fileDTO = FileDTO.fileToDTO(file);
      fileDTOs[files.indexOf(file)] = fileDTO;
    }
    return ResponseEntity.ok(fileDTOs);
  }

  public ResponseEntity<Object> getQuestionsByTitle(String title) {
    Set<Question> questions = questionRepository.findQuestionsByTitleContaining(title);
    if (questions.isEmpty()) {
      return ResponseEntity.ok(new ArrayList<Question>());
    }

    List<QuestionDTO> result = questions.stream().map(QuestionDTO::questionToDTO).toList();

    return ResponseEntity.ok(result);
  }



  public ResponseEntity<Object> publishNoDocQuestion(QuestionInputDTO inputQuestion) {

    Question question = new Question();
    question.setTitle(inputQuestion.getTitle());
    question.setContent(inputQuestion.getContent());
    Optional<User> author = userRepository.findById(inputQuestion.getAuthorId());
    if (author.isEmpty()) {
      return ResponseEntity.status(404).body("user/not-found");
    }
    Optional<Group> group = groupRepository.findById(inputQuestion.getGroupId());
    if (group.isEmpty()) {
      return ResponseEntity.status(404).body("group/not-found");
    }
    author.get().removeCredits(author.get());
    question.setAuthor(author.get());
    question.setGroup(group.get());

    userRepository.save(author.get());
    groupRepository.save(group.get());
    questionRepository.save(question);

    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }



  @Transactional
  public Object publishQuestion(QuestionInputDTO info, List<MultipartFile> files) {

    ResponseEntity<Object> noQuestionInfo = publishNoDocQuestion(info);
    if (noQuestionInfo.getStatusCode() != HttpStatusCode.valueOf(200)) {
      return noQuestionInfo;
    }
    QuestionDTO question = (QuestionDTO) noQuestionInfo.getBody();
    assert question != null;
    String questionId = question.getId();
    return addFiles(files, questionId);
  }





  @Transactional
  public ResponseEntity<Object> deleteQuestion(String id, String userId) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) return ResponseEntity.status(404).body("question/not-found");
    Question question = questionOptional.get();
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) return ResponseEntity.status(404).body("user/not-found");
    User user = userOptional.get();
    if (!question.getAuthor().getId().equals(user.getId()))
      return ResponseEntity.status(403).body("user/not-authorized");
    fileRepository.deleteAll(question.getFiles());
    questionRepository.delete(question);
    return ResponseEntity.ok("question/deleted");
  }

  @Transactional
  public ResponseEntity<Object> editQuestion(
      String id, String title, String content, List<MultipartFile> files) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) return ResponseEntity.status(404).body("question/not-found");
    Question question = questionOptional.get();
    if (title != null) question.setTitle(title);
    if (content != null) question.setContent(content);
    if (files != null) {
      fileRepository.deleteAll(question.getFiles());
      addFiles(files, id);
    }
    questionRepository.save(question);
    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

  @Transactional
  public ResponseEntity<Object> getImages(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();

    List<Map<String, String>> images =
            question.getFiles().stream()
                    .filter(questionFile -> questionFile.getFileType().contains("image"))
                    .map(
                            questionFile -> {
                              Map<String, String> imageMap = new HashMap<>();
                              imageMap.put("fileType", questionFile.getFileType());
                              imageMap.put(
                                      "base64Encoding", Base64.getEncoder().encodeToString(questionFile.getFile()));
                              imageMap.put("name", questionFile.getId());
                              return imageMap;
                            })
                    .collect(Collectors.toList());

    return ResponseEntity.ok(images);
  }



  // ------helper methods------------------------------------------


  @Transactional
  public ResponseEntity<Object> addFiles(List<MultipartFile> images, String questionId) {

    Optional<Question> questionOptional = questionRepository.findById(questionId);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();

    if (images != null) {
      Set<Files> questionFiles =
              images.stream()
                      .map(
                              file -> {
                                Files questionFile = new Files();
                                try {
                                  questionFile.setFile(file.getBytes());
                                  questionFile.setFileName(file.getOriginalFilename());
                                  questionFile.setFileType(file.getContentType());
                                } catch (IOException e) {
                                  e.printStackTrace();
                                }
                                fileRepository.save(questionFile);
                                return questionFile;
                              })
                      .collect(Collectors.toSet());

      question.setFiles(questionFiles);
    }

    questionRepository.save(question);

    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

  }
