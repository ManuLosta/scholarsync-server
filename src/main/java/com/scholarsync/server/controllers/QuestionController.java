package com.scholarsync.server.controllers;

import com.scholarsync.server.dtos.QuestionInputDTO;
import com.scholarsync.server.services.QuestionService;
import io.github.bucket4j.*;
import java.time.Duration;
import java.util.List;
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

  @GetMapping(value = "/get-question")
  public ResponseEntity<Object> getQuestion(String id) {
    return ResponseEntity.ok(questionService.getQuestion(id));
  }

  @GetMapping(value = "/download-files", produces = "application/zip")
  public ResponseEntity<Object> downloadFiles(String id) {
    ResponseEntity<Object> response = questionService.downloadFiles(id);
    return response;
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

  @GetMapping(value = "/download-file")
  public ResponseEntity<Object> downloadFile(String id) {
    ResponseEntity<Object> response = questionService.downloadFile(id);
    MediaType contentType = response.getHeaders().getContentType();
    HttpHeaders headers = response.getHeaders();
    Object body = response.getBody();
    return ResponseEntity.ok().contentType(contentType).headers(headers).body(body);
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
      @RequestParam List<MultipartFile> files) {
    QuestionInputDTO info = new QuestionInputDTO();
    info.setTitle(title);
    info.setContent(content);
    info.setAuthorId(authorId);
    info.setGroupId(groupId);
    return ResponseEntity.ok(questionService.publishQuestion(info, files));
  }

  @PostMapping("/publish-no-doc-question")
  public ResponseEntity<Object> publishNoDocQuestion(@RequestBody QuestionInputDTO inputQuestion) {
    return ResponseEntity.ok(questionService.publishNoDocQuestion(inputQuestion));
  }
}
