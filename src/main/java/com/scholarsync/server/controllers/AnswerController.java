package com.scholarsync.server.controllers;

import com.scholarsync.server.services.AnswerService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/answers")
public class AnswerController {

  @Autowired AnswerService answerService;

  @PostMapping("/answer-question")
  @Transactional
  public ResponseEntity<Object> answerQuestion(
      @RequestParam String questionId,
      @RequestParam String content,
      @RequestParam String userId,
      @RequestParam String groupId,
      @RequestParam(required = false) List<MultipartFile> files) {
    return answerService.answerQuestion(questionId, content, userId, groupId, files);
  }

  @PostMapping("/rate-answer")
  public ResponseEntity<Object> rateAnswer(@RequestBody Map<String, Object> body) {
    String answerId = (String) body.get("answer_id");
    String userId = (String) body.get("user_id");
    int rating = (int) body.get("rating");
    return answerService.rateAnswer(answerId, userId, rating);
  }

  @PostMapping("/delete")
  public ResponseEntity<Object> deleteAnswer(@RequestBody Map<String, String> body) {
    String user_id = body.get("user_id");
    String id = body.get("id");
    return answerService.deleteAnswer(user_id, id);
  }

  @PostMapping("/edit")
  public ResponseEntity<Object> editAnswer(
      @RequestParam String user_id,
      @RequestParam String id,
      @RequestParam String content,
      @RequestParam List<MultipartFile> files) {

    return answerService.editAnswer(user_id, id, content, files);
  }

  @GetMapping("/get-images")
  public ResponseEntity<Object> getImages(@RequestParam String answerId) {
    return answerService.getImages(answerId);
  }

  @GetMapping("/download-file")
  public ResponseEntity<Object> downloadFile(@RequestBody Map<String, String> body) {
    String fileId = body.get("file_id");
    ResponseEntity<Object> standard = answerService.downloadFile(fileId);
    MediaType contentType = standard.getHeaders().getContentType();
    HttpHeaders headers = standard.getHeaders();
    Object responseBody = standard.getBody();
    return ResponseEntity.ok().contentType(contentType).headers(headers).body(responseBody);
  }
}
