package com.scholarsync.server.controllers;


import com.scholarsync.server.services.FeedGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/feeds")
public class FeedController {

    @Autowired
    FeedGeneratorService feedGeneratorService;

    @GetMapping
    public ResponseEntity<Object> getQuestions(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "id") String id,
            @RequestParam(name = "offset") int offset,
            @RequestParam(name = "limit") int limit)
            throws ExecutionException, InterruptedException {

        return switch (type) {
            case "score-user" -> ResponseEntity.ok(feedGeneratorService.getQuestionsByScore(offset, limit, id));
            case "score-group" -> ResponseEntity.ok(feedGeneratorService.getQuestionsByGroup(id, offset, limit));
            case "date-user" ->
                    ResponseEntity.ok(feedGeneratorService.getQuestionsByDateAndUser(id, offset, limit));
            case "date-group" ->
                    ResponseEntity.ok(feedGeneratorService.getQuestionsByDateAndGroup(id, offset, limit));
            default -> ResponseEntity.badRequest().body("Invalid type parameter");
        };
    }
}
