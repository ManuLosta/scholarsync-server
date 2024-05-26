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
      @RequestParam(required = false) List<MultipartFile> files) {
    return answerService.answerQuestion(questionId, content, userId, files);
  }

  @PostMapping("/rate-answer")
  public ResponseEntity<Object> rateAnswer(@RequestBody Map<String, Object> body) {
    String answerId = (String) body.get("answer_id");
    String userId = (String) body.get("sender_id");
    int rating = (int) body.get("rating");
    return answerService.rateAnswer(answerId, userId, rating);
  }

  @PostMapping("/delete")
  public ResponseEntity<Object> deleteAnswer(@RequestBody Map<String, String> body) {
    String user_id = body.get("sender_id");
    String id = body.get("id");
    return answerService.deleteAnswer(user_id, id);
  }

  @PostMapping("/edit")
  public ResponseEntity<Object> editAnswer(
      @RequestParam String userId,
      @RequestParam String answerId,
      @RequestParam String content,
      @RequestParam(required = false) List<MultipartFile> files) {

    return answerService.editAnswer(userId, answerId, content, files);
  }

  @GetMapping("/get-images")
  public ResponseEntity<Object> getImages(@RequestParam String answerId) {
    return answerService.getImages(answerId);
  }

  @GetMapping("answers-by-question")
    public ResponseEntity<Object> getAnswersByQuestion(@RequestParam String questionId) {
        return answerService.getAnswersByQuestion(questionId);
    }
}
