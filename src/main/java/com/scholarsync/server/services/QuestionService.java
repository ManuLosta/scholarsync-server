package com.scholarsync.server.services;

import com.scholarsync.server.entities.Question;
import com.scholarsync.server.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class QuestionService {

  @Autowired QuestionRepository questionRepository;

  public Question getQuestion(String id) {
    Optional<Question> questionOptional = questionRepository.findById(id);
      return questionOptional.orElse(null);
  }

  public Set<Question> getQuestionsByTitle(String title) {
    return questionRepository.findQuestionsByTitleContaining(title);
  }
}
