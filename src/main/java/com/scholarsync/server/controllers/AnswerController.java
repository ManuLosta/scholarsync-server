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

  @PostMapping("/upvote")
  public ResponseEntity<Object> upVoteAnswer(@RequestBody Map<String, String> body) {
    String answerId = body.get("answerId");
    return answerService.upVoteAnswer(answerId);
  }

  @PostMapping("/downvote")
  public ResponseEntity<Object> downVoteAnswer(@RequestBody Map<String, String> body) {
    String answerId = body.get("answerId");
    return answerService.downVoteAnswer(answerId);
  }

  @PostMapping("/edit")
  public ResponseEntity<Object> editAnswer(@RequestBody Map<String, String> body) {
    String answerId = body.get("answer_id");
    String content = body.get("content");
    return answerService.editAnswer(answerId, content);
  }

  @GetMapping("/get-images")
  public ResponseEntity<Object> getImages(@RequestBody Map<String, String> body) {
    String answerId = body.get("answer_id");
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
