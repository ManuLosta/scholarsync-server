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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

  @Autowired QuestionRepository questionRepository;
  @Autowired UserRepository userRepository;
  @Autowired
  private GroupRepository groupRepository;
  @Autowired
  private QuestionFileRepository questionFileRepository;

  public ResponseEntity<Object> getQuestion(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
    if (questionOptional.isEmpty()) {
      return ResponseEntity.status(404).body("question/not-found");
    }
    Question question = questionOptional.get();

    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

  public ResponseEntity<Object> getQuestionsByTitle(String title) {
    Set<Question> questions = questionRepository.findQuestionsByTitleContaining(title);
    if (questions.isEmpty()) {
      return ResponseEntity.ok(new ArrayList<Question>());
    }
    List<QuestionDTO> result = questions.stream().map(QuestionDTO::questionToDTO).toList();

    return ResponseEntity.ok(result);
  }

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

    List<Byte[]> files  = inputQuestion.getFiles();
    if (files != null) {
      Set<QuestionFiles> questionFiles = files.stream().map(file -> {
        QuestionFiles questionFile = new QuestionFiles();
        questionFile.setFile(file);
        questionFile.setQuestion(question);
        questionFileRepository.save(questionFile);
        return questionFile;
      }).collect(Collectors.toSet());

      question.setQuestionFiles(questionFiles);
    }

    questionRepository.save(question);


    return ResponseEntity.ok(QuestionDTO.questionToDTO(question));
  }

}
