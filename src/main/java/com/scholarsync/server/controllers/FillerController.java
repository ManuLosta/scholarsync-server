package com.scholarsync.server.controllers;

import com.scholarsync.server.services.DbFillerService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/filler")
public class FillerController {

  @Autowired DbFillerService fillerService;

  @PostMapping("/do-fill")
  public ResponseEntity<Object> filler(@RequestBody Map<String, String> fillerRequestBody) {
    if (!fillerRequestBody.get("secret").equals("d3v3l0p3r")) {
      return ResponseEntity.badRequest().build();
    }
    fillerService.fillDatabase();
    return ResponseEntity.ok().build();
  }
}
