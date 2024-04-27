package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.QuestionInputDTO;
import com.scholarsync.server.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.bucket4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {
  private final QuestionService questionService;
  private final Bucket bucket;

  public QuestionController(QuestionService questionService) {
    this.questionService = questionService;
    Bandwidth bandwidth =
        Bandwidth.builder().capacity(10).refillIntervally(8, Duration.ofSeconds(1)).build();
    this.bucket = Bucket.builder().addLimit(bandwidth).build();
  }

  @GetMapping("/get-questions-by-title")
  public ResponseEntity<Object> getQuestionsByTitle(String title) {
    try {
      bucket.tryConsume(1);
      return ResponseEntity.ok(questionService.getQuestionsByTitle(title));
    } catch (BucketExceptions.BucketExecutionException e) {
      return ResponseEntity.status(429).body("Too many requests");
    }
  }

  @GetMapping("/get-question")
  public ResponseEntity<Object> getQuestion(String id) {
    return ResponseEntity.ok(questionService.getQuestion(id));
  }

  @PostMapping("/publish-question")
  public ResponseEntity<Object> publishQuestion(QuestionInputDTO inputQuestion) {
    return ResponseEntity.ok(questionService.publishQuestion(inputQuestion));
  }
}
