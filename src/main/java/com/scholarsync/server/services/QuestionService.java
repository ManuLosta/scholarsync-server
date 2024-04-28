package com.scholarsync.server.services;

import com.scholarsync.server.dtos.QuestionDTO;
import com.scholarsync.server.dtos.QuestionInputDTO;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.Question;
import com.scholarsync.server.entities.QuestionFiles;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.QuestionFileRepository;
import com.scholarsync.server.repositories.QuestionRepository;
import com.scholarsync.server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class QuestionService {

  @Autowired QuestionRepository questionRepository;
  @Autowired UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private QuestionFileRepository questionFileRepository;

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
  public ResponseEntity<Object> downloadFiles(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body(null);
    }
    Question question = questionOptional.get();
    List<QuestionFiles> files = new ArrayList<>(question.getQuestionFiles());
    String questionId = question.getId();

    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ZipOutputStream zos = new ZipOutputStream(baos);
      for (QuestionFiles questionFile : files) {
        ZipEntry entry = new ZipEntry(questionFile.getId());
        entry.setSize(questionFile.getFile().length);
        zos.putNextEntry(entry);
        zos.write(questionFile.getFile());
        zos.closeEntry();
      }
      zos.close();
      byte[] zipBytes = baos.toByteArray();

      return ResponseEntity.ok()
          .header(
              HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + questionId + ".zip\"")
          .body(zipBytes);
    } catch (IOException e) {
      return ResponseEntity.status(500).body(null);
    }
  }

  public ResponseEntity<Object> getQuestionsByTitle(String title) {
    Set<Question> questions = questionRepository.findQuestionsByTitleContaining(title);
    if (questions.isEmpty()) {
      return ResponseEntity.ok(new ArrayList<Question>());
    }

    List<QuestionDTO> result = questions.stream().map(QuestionDTO::questionToDTO).toList();

    return ResponseEntity.ok(result);
  }

  @Transactional
  public ResponseEntity<Object> publishQuestion(QuestionInputDTO inputQuestion) {
    Question question = new Question();
    question.setTitle(inputQuestion.getTitle());
    question.setContent(inputQuestion.getContent());
    String authorId = inputQuestion.getAuthorId();
    String groupId = inputQuestion.getGroupId();
    Optional<User> author = userRepository.findById(authorId);
    if (author.isEmpty()) {
      return ResponseEntity.status(404).body("user/not-found");
    }
    Optional<Group> group = groupRepository.findById(groupId);
    if (group.isEmpty()) {
      return ResponseEntity.status(404).body("group/not-found");
    }
    question.setAuthor(author.get());
    question.setGroup(group.get());

    List<MultipartFile> files = inputQuestion.getFiles();

    questionRepository.save(question);

    if (files != null) {
      Set<QuestionFiles> questionFiles =
          files.stream()
              .map(
                  file -> {
                    QuestionFiles questionFile = new QuestionFiles();
                    try {
                      questionFile.setFile(file.getBytes());
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    questionFile.setQuestion(question);
                    questionFileRepository.save(questionFile);
                    return questionFile;
                  })
              .collect(Collectors.toSet());

      question.setQuestionFiles(questionFiles);
    }

    questionRepository.save(question);

    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

  public ResponseEntity<Object> publishNoDocQuestion(Map<String,Object> inputQuestion) {

    String title = inputQuestion.get("title").toString();
    String content = inputQuestion.get("content").toString();
    String authorId = inputQuestion.get("authorId").toString();
    String groupId = inputQuestion.get("groupId").toString();
    Question question = new Question();
    question.setTitle(title);
    question.setContent(content);
    Optional<User> author = userRepository.findById(authorId);
    if (author.isEmpty()) {
      return ResponseEntity.status(404).body("user/not-found");
    }
    Optional<Group> group = groupRepository.findById(groupId);
    if (group.isEmpty()) {
      return ResponseEntity.status(404).body("group/not-found");
    }
    question.setAuthor(author.get());
    question.setGroup(group.get());

    questionRepository.save(question);


    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }
}
