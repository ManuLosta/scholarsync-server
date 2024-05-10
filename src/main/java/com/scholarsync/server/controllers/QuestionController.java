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
  public ResponseEntity<Object> editQuestion(@RequestBody Map<String, String> body) {
    String questionId = body.get("question_id");
    String title = body.get("title");
    String content = body.get("content");
    return ResponseEntity.ok(questionService.editQuestion(questionId, title, content));
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
    return questionService.publishNoDocQuestion(inputQuestion);
  }

  @GetMapping("/get-questions-by-score")
  public ResponseEntity<Object> getQuestionsByScore(
      @RequestParam(name = "offset") int offset,
      @RequestParam(name = "limit") int limit,
      @RequestParam(name = "user_id") String userId)
      throws ExecutionException, InterruptedException {
    return ResponseEntity.ok(questionService.getQuestionsByScore(offset, limit, userId));
  }

  @GetMapping("/get-answers-by-question")
  public ResponseEntity<Object> getAnswersByQuestion(
      @RequestParam(name = "question_id") String questionId) {
    return ResponseEntity.ok(questionService.getAnswersByQuestion(questionId));
  }

  @PostMapping("/delete-question")
  public ResponseEntity<Object> deleteQuestion(@RequestBody Map<String, String> body) {
    String questionId = body.get("question_id");
    return ResponseEntity.ok(questionService.deleteQuestion(questionId));
  }

  @PostMapping("/delete-files")
  public ResponseEntity<Object> deleteFiles(@RequestBody Map<String, Object> body) {
    String questionId = (String) body.get("question_id");
    List<String> files = (List<String>) body.get("files");
    return ResponseEntity.ok(questionService.deleteFiles(questionId, files));
  }
}
