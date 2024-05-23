package com.scholarsync.server.controllers;

import com.scholarsync.server.services.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/downloads")
public class DownloadController {

  @Autowired DownloadService downloadService;

  @GetMapping
  public ResponseEntity<Object> downloadFile(
      @RequestParam String id, @RequestParam boolean isQuestion) {
    ResponseEntity<Object> response = downloadService.downloadFile(id, isQuestion);
    if (response.getStatusCode().isError()) return response;
    MediaType contentType = response.getHeaders().getContentType();
    HttpHeaders headers = response.getHeaders();
    Object body = response.getBody();
    return ResponseEntity.ok().contentType(contentType).headers(headers).body(body);
  }
}
