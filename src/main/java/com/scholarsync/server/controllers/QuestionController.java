package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.QuestionInputDTO;
import com.scholarsync.server.services.QuestionService;
import io.github.bucket4j.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

  @PostMapping("/edit-question")
  public ResponseEntity<Object> editQuestion(
      @RequestParam String id,
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam(required = false) List<MultipartFile> files) {
    return ResponseEntity.ok(questionService.editQuestion(id, title, content, files));
  }

  @GetMapping(value = "/get-question")
  public ResponseEntity<Object> getQuestion(String id) {
    return ResponseEntity.ok(questionService.getQuestion(id));
  }


  @GetMapping("/get-images")
  public ResponseEntity<Object> getImages(String id) {
    return ResponseEntity.ok(questionService.getImages(id));
  }

  @PostMapping("/upload-images")
  public ResponseEntity<Object> uploadImages(
      @RequestParam("files") List<MultipartFile> files, @RequestParam String questionId) {
    return ResponseEntity.ok(questionService.addFiles(files, questionId));
  }


  @GetMapping("/get-question-files")
  public ResponseEntity<Object> getQuestionFiles(String id) {
    return ResponseEntity.ok(questionService.getFiles(id));
  }

  @PostMapping("/publish-question")
  public ResponseEntity<Object> publishQuestion(
      @RequestParam String title,
      @RequestParam String content,
      @RequestParam String authorId,
      @RequestParam String groupId,
      @RequestParam(required = false) List<MultipartFile> files) {
    QuestionInputDTO info = new QuestionInputDTO();
    info.setTitle(title);
    info.setContent(content);
    info.setAuthorId(authorId);
    info.setGroupId(groupId);
    return ResponseEntity.ok(questionService.publishQuestion(info, files));
  }


  @PostMapping("/delete-question")
  public ResponseEntity<Object> deleteQuestion(@RequestBody Map<String, String> body) {
    String questionId = body.get("question_id");
    String userId = body.get("user_id");
    return ResponseEntity.ok(questionService.deleteQuestion(userId, questionId));
  }
}
